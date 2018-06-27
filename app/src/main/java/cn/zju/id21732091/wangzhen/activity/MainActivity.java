package cn.zju.id21732091.wangzhen.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;

import cn.zju.id21732091.wangzhen.R;
import cn.zju.id21732091.wangzhen.TweetApplication;
import cn.zju.id21732091.wangzhen.adapter.StatusAdapter;
import cn.zju.id21732091.wangzhen.db.DbHelper;
import cn.zju.id21732091.wangzhen.db.StatusContract;
import cn.zju.id21732091.wangzhen.pojo.StatusInfo;
import cn.zju.id21732091.wangzhen.service.UpdateService;
import cn.zju.id21732091.wangzhen.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRvStatusList;
    private ArrayList<StatusInfo> statusList;
    private CircleRefreshLayout mCrlPullToRefresh;

    SQLiteDatabase db;

    DbHelper dbHelper;
    private String imgURL;
    //只下载一次头像
    private boolean downloadFlag = false;
    private static final int ACTION_EXIT = 0;
    private static final int ACTION_FINISH_REFRESH = 1;
    private static final int ACTION_REFRESH = 2;
    private static boolean isExit = false;// 定义一个变量，来标识是否退出
    private boolean serviceRunning = false;
    private int updateNum = 0;

    private StatusAdapter mStatusAdapter;
    private int lastStatusListSize;

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == ACTION_FINISH_REFRESH ){
                mStatusAdapter.setStatusList(statusList);
                mCrlPullToRefresh.finishRefreshing();
            }else if (msg.what == ACTION_REFRESH){


                statusList = loadData(db);
                mStatusAdapter.setStatusList(statusList);
                Toast.makeText(getApplicationContext(),"更新"+String.valueOf(statusList.size()-lastStatusListSize)+"条",Toast.LENGTH_SHORT).show();
            }else if (msg.what == ACTION_EXIT){
                isExit = false;
            }
        };
    };
    private Cursor cursor;

    private TimelineReceiver receiver;
    private IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();

        statusList = loadData(db);
        lastStatusListSize = statusList.size();

        mRvStatusList = findViewById(R.id.rv_status);
        mRvStatusList.setLayoutManager(new LinearLayoutManager(this));
        mStatusAdapter = new StatusAdapter(this,statusList);
        mRvStatusList.setAdapter(mStatusAdapter);
        mStatusAdapter.setOnItemClickListener(new StatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                StatusInfo statusInfo = statusList.get(position);

                new AlertDialog.Builder(MainActivity.this).setTitle(statusInfo.getUserId())
                        .setMessage(statusInfo.getContent())
                        .setIcon(new BitmapDrawable(statusInfo.getUserImg()))
                        .setNegativeButton("关闭",null)
                        .show();


            }
        });


        receiver = new TimelineReceiver();
        filter = new IntentFilter(StatusContract.NEW_STATUSES);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mCrlPullToRefresh.finishRefreshing();
               startActivity(new Intent(getApplicationContext(),StatusActivity.class));
            }
        });


        mCrlPullToRefresh = findViewById(R.id.crl_pull_to_refresh);
        //下拉刷新监听事件
        mCrlPullToRefresh.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {
                Toast.makeText(getApplicationContext(),"更新"+String.valueOf(updateNum)+"条",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void refreshing() {
                lastStatusListSize = statusList.size();

                // reset the update service
                stopService(new Intent(getApplicationContext(), UpdateService.class));
                serviceRunning = false;
                startService(new Intent(getApplicationContext(),UpdateService.class));
                serviceRunning = true;
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(2000);
                            statusList = loadData(db);
                            updateNum = statusList.size() - lastStatusListSize;
                            Message msg = new Message();
                            msg.what = ACTION_FINISH_REFRESH;
                            handler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }



    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    /**
     * 从数据库中加载数据
     * @param db
     * @return
     */
    private ArrayList<StatusInfo> loadData(SQLiteDatabase db) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return null;
        }else {
            cursor = db.query(StatusContract.TABLE, null, null, null, null, null, StatusContract.DEFAULT_SORT);
            ArrayList<StatusInfo> list = new ArrayList<StatusInfo>();
            Bitmap userImg = null;
            while (cursor.moveToNext()) {
                String id = cursor.getString(1);
                String createAt = cursor.getString(3);
                String content = cursor.getString(2);


                userImg = ImageUtils.readImage(getFilesDir().getPath() + "/" + id + ".png");

                if(content.length()>13){
                    content = content.substring(0, content.length() - 13);
                }

                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(Long.parseLong(createAt));


                StatusInfo statusInfo = new StatusInfo(id, relativeTime.toString(), content, userImg);
                statusInfo.setUserImgUrl(cursor.getString(4));
                list.add(statusInfo );

            }
            return list;
        }
    }


    /**
     * 菜单栏的点击事件
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(3).setVisible(false);
        menu.getItem(7).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu == null) return true;
        serviceRunning = ((TweetApplication)getApplication()).serviceRunning;
        MenuItem toggleItem = menu.findItem(R.id.action_start_service);
        toggleItem.setChecked(serviceRunning);

        if(serviceRunning){
            toggleItem.setTitle(R.string.stop_service);

        }else {
            toggleItem.setTitle(R.string.start_service);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
          // new SubmitProgram().doSubmit(this,"G2");
           Toast.makeText(this,R.string.pause_submit,Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_calculator) {
            startActivity(new Intent(MainActivity.this,CalculatorActivity.class));
            return true;
        } else if (id == R.id.action_status) {

            startActivity(new Intent(MainActivity.this, StatusActivity.class));
            return true;
        } else if (id == R.id.action_file_storage){
            startActivity(new Intent(this,FileStorageActivity.class));
            return true;
        } else if(id == R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        } else if(id == R.id.action_add_status){
            stopService(new Intent(getApplicationContext(), UpdateService.class));
            serviceRunning = false;
            startService(new Intent(getApplicationContext(),UpdateService.class));
            serviceRunning = true;
            Message msg = new Message();
            msg.what = ACTION_REFRESH;
            handler.sendMessage(msg);
            //startActivity(new Intent(this,StatusActivity.class));
            return true;
        } else if(id == R.id.action_music_list){
            startActivity(new Intent(this,MusicPlayerActivity.class));
            return true;
        } else if(id == R.id.action_start_service){
            if(serviceRunning) {
                stopService(new Intent(this, UpdateService.class));
                serviceRunning = false;
            }else{
                startService(new Intent(this,UpdateService.class));
                serviceRunning = true;
            }
            return true;
        } else if(id == R.id.action_delete){
            db.delete(StatusContract.TABLE, null, null);
            cursor.requery();
            statusList = loadData(db);
            mStatusAdapter.setStatusList(statusList);
            Toast.makeText(this,"已清除数据",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
       点击两次返回才退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }

    }

    class TimelineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("count",0);
            if(count>0){
                cursor.requery();
                statusList = loadData(db);
                mStatusAdapter.setStatusList(statusList);
               // mStatusAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"更新了"+count+"条记录。",Toast.LENGTH_SHORT).show();
            }


        }
    }

}

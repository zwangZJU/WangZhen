package cn.zju.id21732091.wangzhen.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.VoicemailContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cn.iipc.android.tweetlib.SubmitProgram;
import cn.zju.id21732091.wangzhen.R;
import cn.zju.id21732091.wangzhen.adapter.StatusAdapter;
import cn.zju.id21732091.wangzhen.db.DbHelper;
import cn.zju.id21732091.wangzhen.db.StatusContract;
import cn.zju.id21732091.wangzhen.pojo.MusicInfo;
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
                startService(new Intent(new Intent(MainActivity.this,UpdateService.class)));
                statusList = loadData(db);
                mStatusAdapter.setStatusList(statusList);
                Toast.makeText(getApplicationContext(),"更新"+String.valueOf(statusList.size()-lastStatusListSize)+"条",Toast.LENGTH_SHORT).show();
            }else if (msg.what == ACTION_EXIT){
                isExit = false;
            }
        };
    };



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
                startService(new Intent(getApplicationContext(),UpdateService.class));
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


    /**
     * 从数据库中加载数据
     * @param db
     * @return
     */
    private ArrayList<StatusInfo> loadData(SQLiteDatabase db) {
        Cursor cursor = db.query(StatusContract.TABLE,null,null,null,null,null,StatusContract.DEFAULT_SORT);
        ArrayList<StatusInfo> list = new ArrayList<StatusInfo>();
        Bitmap userImg = null;
        while(cursor.moveToNext()){
            String id = cursor.getString(1);
            String createAt = cursor.getString(3);
            String content = cursor.getString(2);
            if(userImg == null){
                userImg = ImageUtils.readImage(getFilesDir().getPath()+"/"+id+".png");
            }
            content = content.substring(0,content.length()-13);
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(Long.parseLong(createAt));

            if(userImg!=null){
                list.add(new StatusInfo(id,relativeTime.toString(),content,userImg));
            }else {
                // Toast.makeText(getApplicationContext(),"头像未加载，请稍后再试",Toast.LENGTH_SHORT).show();
                list.add(new StatusInfo(id,relativeTime.toString(),content,null));
            }
        }
        return list;
    }


    /**
     * 菜单栏的点击事件
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(3).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
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
            Message msg = new Message();
            msg.what = ACTION_REFRESH;
            handler.sendMessage(msg);
            //startActivity(new Intent(this,StatusActivity.class));
            return true;
        } else if(id == R.id.action_music_list){
            startActivity(new Intent(this,MusicListActivity.class));
            return true;
        } else if(id == R.id.action_start_service){
            startService(new Intent(this,UpdateService.class));
            return true;
        } else if(id == R.id.action_stop_service){
            stopService(new Intent(this,UpdateService.class));
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

}

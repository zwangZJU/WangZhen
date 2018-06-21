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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

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

    SQLiteDatabase db;
    Cursor cursor;
    DbHelper dbHelper;
    private String imgURL;
    private Bitmap userImg;
    private boolean downloadFlag = false;

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==111){
            //    mRvStatusList.getAdapter().notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"ddd",Toast.LENGTH_SHORT).show();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
        cursor = db.query(StatusContract.TABLE,null,null,null,null,null,StatusContract.DEFAULT_SORT);
        statusList = new ArrayList<StatusInfo>();



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
                statusList.add(new StatusInfo(id,relativeTime.toString(),content,userImg));
            }else {
               // Toast.makeText(getApplicationContext(),"头像未加载，请稍后再试",Toast.LENGTH_SHORT).show();
                statusList.add(new StatusInfo(id,relativeTime.toString(),content,null));
            }


        }


        mRvStatusList = findViewById(R.id.rv_status);
        mRvStatusList.setLayoutManager(new LinearLayoutManager(this));
        StatusAdapter statusAdapter = new StatusAdapter(this,statusList);
        mRvStatusList.setAdapter(statusAdapter);
        statusAdapter.setOnItemClickListener(new StatusAdapter.OnItemClickListener() {
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
                startActivity(new Intent(getApplicationContext(),StatusActivity.class));
            }
        });


    }

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
         //   new SubmitProgram().doSubmit(this,"G2");
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
            startActivity(new Intent(this,StatusActivity.class));
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



}

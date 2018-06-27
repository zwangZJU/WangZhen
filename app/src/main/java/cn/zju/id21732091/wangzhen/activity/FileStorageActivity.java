package cn.zju.id21732091.wangzhen.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.zju.id21732091.wangzhen.R;

public class FileStorageActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnGetFilesDir;
    private Button mBtnGetExtStorDir;
    private Button mBtnGetExtFilesDir;
    private TextView mTvShowResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_storage);

        mBtnGetFilesDir = findViewById(R.id.btn_get_files_dir);
        mBtnGetExtStorDir = findViewById(R.id.btn_get_external_storage_directory);
        mBtnGetExtFilesDir = findViewById(R.id.btn_get_external_files_dir);
        mTvShowResult = findViewById(R.id.tv_show_result);

        mBtnGetFilesDir.setOnClickListener(this);
        mBtnGetExtStorDir.setOnClickListener(this);
        mBtnGetExtFilesDir.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnGetFilesDir){
            fileWriter(getFilesDir().getPath());
        }else if(view == mBtnGetExtStorDir){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else {
                fileWriter(Environment.getExternalStorageDirectory().getPath());
            }

        }else if(view == mBtnGetExtFilesDir){
            fileWriter(getExternalFilesDir(null).getPath());
        }

    }

    private void fileWriter(String dir){
        String fn = dir + "/wz.txt";
        mTvShowResult.setText(mTvShowResult.getText() + " \nWrite to: "+fn);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fn)));
            pw.println(fn);
            pw.close();
        } catch (IOException e) {
            mTvShowResult.setText(mTvShowResult.getText()+ "\nWrite to"+ e.toString());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(2).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_close){
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_play_music) {
           // new SubmitProgram().doSubmit(this,"E1");
            startActivity(new Intent(this,MusicPlayerActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_calculator) {
            startActivity(new Intent(this,CalculatorActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_status) {
            startActivity(new Intent(this, StatusActivity.class));
            finish();
            return true;
        }else if(id == R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

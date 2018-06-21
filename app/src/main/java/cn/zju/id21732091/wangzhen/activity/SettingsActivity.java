package cn.zju.id21732091.wangzhen.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.iipc.android.tweetlib.SubmitProgram;
import cn.zju.id21732091.wangzhen.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(3).setVisible(false);
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
        if (id == R.id.action_submit) {
           new SubmitProgram().doSubmit(this,"E2");
           // Toast.makeText(this, R.string.pause_submit,Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_calculator) {
            startActivity(new Intent(this,CalculatorActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_status) {
            startActivity(new Intent(this, StatusActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_file_storage){
            startActivity(new Intent(this,FileStorageActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

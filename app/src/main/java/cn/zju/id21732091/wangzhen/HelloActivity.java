package cn.zju.id21732091.wangzhen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
//            new SubmitProgram().doSubmit(this,"A1");
            Toast.makeText(this,R.string.pause_submit,Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_calculator) {
            startActivity(new Intent(HelloActivity.this,CalculatorActivity.class));
            return true;
        } else if (id == R.id.action_status) {
            startActivity(new Intent(HelloActivity.this, StatusActivity.class));
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

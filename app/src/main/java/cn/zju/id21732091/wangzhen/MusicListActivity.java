package cn.zju.id21732091.wangzhen;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import cn.iipc.android.tweetlib.SubmitProgram;

public class MusicListActivity extends AppCompatActivity {

    private TextView mTvMusicList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        mTvMusicList = findViewById(R.id.et_music_list);

        ContentResolver provider = getContentResolver();
        Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{"artist","title","duration"},null,null,null);
        String s = "";
        while(cursor.moveToNext()){
            s += String.format("作者：%s   歌名：%s    时长：%d(秒)\n\n",
                    cursor.getString(0),cursor.getString(1),Integer.parseInt(cursor.getString(2))/1000);
        }
        mTvMusicList.setText(s);

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
        if (id == R.id.action_submit) {
            //new SubmitProgram().doSubmit(this,"F1");
            Toast.makeText(this, R.string.pause_submit,Toast.LENGTH_SHORT).show();
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

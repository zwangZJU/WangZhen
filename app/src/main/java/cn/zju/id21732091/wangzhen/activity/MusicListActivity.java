package cn.zju.id21732091.wangzhen.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.zju.id21732091.wangzhen.R;
import cn.zju.id21732091.wangzhen.adapter.MusicAdapter;
import cn.zju.id21732091.wangzhen.pojo.MusicInfo;

public class MusicListActivity extends AppCompatActivity {

    private RecyclerView mRvMusicList;
  //  private TextView mTvMusicList;
    private ArrayList<MusicInfo> musicList;
    private MediaPlayer player = new MediaPlayer();
    private ImageView mIvBarAlbumImg;
    private TextView mTvBarMusicTitle;
    private TextView mTvBarArtist;
    private boolean isPlay = false;
    private boolean hasToched = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int positon = msg.what;
            MusicInfo musicInfo = musicList.get(positon);
            String artist = musicInfo.getArtist();
//            mIvBarAlbumImg = findViewById(R.id.iv_bottom_bar_album_img);
//            mTvBarArtist = findViewById(R.id.tv_bottom_bar_artist);
//            mTvBarMusicTitle = findViewById(R.id.tv_bottom_bar_music_title);
            mTvBarArtist.setText(musicInfo.getArtist());
            mTvBarMusicTitle.setText(musicInfo.getTitle());
            Bitmap bitmap = getMusicBitmap(getApplicationContext(),musicInfo.getID(),musicInfo.getAlbumId());
              mIvBarAlbumImg.setImageBitmap(bitmap);

            // musicInfo.setAlbumImg(bitmap);
        }
    };
    private ImageView mIvPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            // 获取数据
            ContentResolver provider = getContentResolver();
            Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"artist", "title", "duration", "_id", "album_id", "album", MediaStore.Audio.Media.DATA}, null, null, null);

            musicList = new ArrayList<>();
            while(cursor.moveToNext()){
                int t = Integer.parseInt(cursor.getString(2))/1000;
                String min = String.valueOf(t/60);
                String sec = String.valueOf(t%60);
                if (sec.length()==1){
                    sec = "0"+sec;
                }

                musicList.add(new MusicInfo(cursor.getInt(3),cursor.getString(0),cursor.getString(1),"  |  " + min+":"+ sec, cursor.getInt(4),cursor.getString(5),cursor.getString(6)));

            }
        }
        // 用RecyclerView显示数据
        setContentView(R.layout.activity_music_list);

        mIvBarAlbumImg = findViewById(R.id.iv_bottom_bar_album_img);
        mTvBarArtist = findViewById(R.id.tv_bottom_bar_artist);
        mTvBarMusicTitle = findViewById(R.id.tv_bottom_bar_music_title);
        if(musicList.size()>0) {
            mTvBarArtist.setText(musicList.get(0).getArtist());
            mTvBarMusicTitle.setText(musicList.get(0).getTitle());
            Bitmap bitmap = getMusicBitmap(getApplicationContext(), musicList.get(0).getID(), musicList.get(0).getAlbumId());
            mIvBarAlbumImg.setImageBitmap(bitmap);
        }else{
            mTvBarArtist.setText("没有音乐");
            //mTvBarMusicTitle.setText(musicList.get(0).getTitle());
        }

        mRvMusicList = findViewById(R.id.rv_music_list);
        mRvMusicList.setLayoutManager(new LinearLayoutManager(this));
        MusicAdapter musicAdapter = new MusicAdapter(this, musicList);
        mRvMusicList.setAdapter(musicAdapter);

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              //  Toast.makeText(getApplicationContext(),String.valueOf(musicList.get(position).uri),Toast.LENGTH_SHORT).show();
//                mImBarAlbumImg.setImageBitmap(musicInfo.getAlbumImg());
//                mTvBarArtist.setText(musicInfo.getArtist());
//                mTvBarMusicTitle.setText(musicInfo.getTitle());
                hasToched = true;
                Message message = new Message();
                message.what = position;
                handler.sendMessage(message);
                player.reset();
                try {
                    player.setDataSource(musicList.get(position).uri);
                    player.prepare();
                    player.start();
                    isPlay = true;
                    mIvPlay = findViewById(R.id.iv_play);
                    mIvPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_music));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"播放出现异常",Toast.LENGTH_SHORT).show();
                }
            }
        });


        mIvPlay = findViewById(R.id.iv_play);
        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlay){
                    mIvPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_music));
                    isPlay = false;
                    player.pause();

                }else {
                    mIvPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_music));
                    isPlay = true;
                    if(hasToched){
                        player.start();
                    }else{
                        player.reset();
                        try {
                            player.setDataSource(musicList.get(0).uri);
                            player.prepare();
                            player.start();
                            isPlay = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"播放出现异常",Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
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
          // new SubmitProgram().doSubmit(this,"G1");
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
    public Bitmap getMusicBitmap(Context context, long id, long albumId) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Bitmap bm = null;
// 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumId < 0 && id < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        try {
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + id + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);


                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
        }
//如果获取的bitmap为空，则返回一个默认的bitmap
        if (bm == null) {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.ic_album_img_default);
            //Drawable 转 Bitmap
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bm = bitmapDrawable.getBitmap();
        }

        return Bitmap.createScaledBitmap(bm, 150, 150, true);
    }
}

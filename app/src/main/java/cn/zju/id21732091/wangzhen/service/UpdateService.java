package cn.zju.id21732091.wangzhen.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.iipc.android.tweetlib.Status;
import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;
import cn.zju.id21732091.wangzhen.TweetApplication;
import cn.zju.id21732091.wangzhen.db.DbHelper;
import cn.zju.id21732091.wangzhen.db.StatusContract;
import cn.zju.id21732091.wangzhen.utils.ImageUtils;

public class UpdateService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "UpdaterService";
    static long DELAY = 60000;
    static String username;
    static String password;
    private boolean runFlag = false;
    private Updater updater;
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        username = sp.getString("username","");
        password = sp.getString("password","");
        DELAY = Long.parseLong(sp.getString("refresh_period","60")) * 1000;
        sp.registerOnSharedPreferenceChangeListener(this);

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            username = "student";
            password = "password";
        }

        this.updater = new Updater();
        Log.d(TAG,"onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag){
            ((TweetApplication)getApplication()).serviceRunning = runFlag = true;
            this.updater.start();
        }
        Log.d(TAG,"onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((TweetApplication)getApplication()).serviceRunning = runFlag = false;
        this.updater.interrupt();
        this.updater = null;
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

        if(key.equals("username")){
            username = sp.getString("username","student");
        }
        if(key.equals("password")){
            password = sp.getString("password","password");
        }
        if(key.equals("refresh_period")){
            DELAY = Long.parseLong(sp.getString("refresh_period","10")) * 1000;
        }
    }
   private class Updater extends Thread {
        public Updater(){
            super("UpdaterService-Thread");
        }

        @Override
        public void run() {
            DbHelper dbHelper = new DbHelper(UpdateService.this);
            while(runFlag){
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                YambaClient cloud = new YambaClient(username,password);
                try {
                    List<Status> timeline = cloud.getTimeline(20);
                    ContentValues values = new ContentValues();
                    int count = 0;
                    long rowID = 0;
                    for(Status status : timeline){
                        String usr = status.getUser();
                        String msg = status.getMessage().trim();
                        String imgURL = "https"+status.getImgUrl().substring(4);
                        values.clear();
                        values.put(StatusContract.Column.ID,status.getId());
                        values.put(StatusContract.Column.USER,usr);
                        values.put(StatusContract.Column.MESSAGE,msg);
                        values.put(StatusContract.Column.CREATED_AT,status.getCreatedAt().getTime());
                        values.put(StatusContract.Column.IMG,imgURL);

                        new DownloadImageTask().execute(imgURL,getFilesDir().getPath(),usr);

                        rowID = db.insertWithOnConflict(StatusContract.TABLE, null,values,SQLiteDatabase.CONFLICT_IGNORE);
                        if(rowID != -1){
                            count++;
                            Log.d(TAG,String.format("%s: %s",usr,msg));
                        }

                    }
                    if(count >= 0){
                        Intent bcast = new Intent(StatusContract.NEW_STATUSES);
                        bcast.putExtra("count",count);
                        sendBroadcast(bcast);
                    }
                } catch (YambaClientException e) {
                    e.printStackTrace();
                    Log.d(TAG,"Failed to fetch the timeline",e);
                } finally {
                    db.close();
                }

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    runFlag = false;
                }
            }
        }
    }

    class DownloadImageTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            Bitmap bitmap= ImageUtils.getImage((String)params[0]);
            if(bitmap != null){
                ImageUtils.savaImage(bitmap,(String)params[1],(String)params[2]);
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

}

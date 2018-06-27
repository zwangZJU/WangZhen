package cn.zju.id21732091.wangzhen;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by wzlab on 2018/6/27.
 */

public class TweetApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener{

    public boolean serviceRunning;
    public long DELAY = 60000;
    public String username;
    public String password;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DELAY = Long.parseLong(prefs.getString("refresh_period", "60")) * 1000;
        username = prefs.getString("username","student");
        password = prefs.getString("password", "password");
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("username")) username = prefs.getString("username","student");
        if (key.equals("password")) password = prefs.getString("password", "password");
        if (key.equals("refresh_period")) DELAY = Long.parseLong(prefs.getString("refresh_period", "60")) * 1000;
    }
}

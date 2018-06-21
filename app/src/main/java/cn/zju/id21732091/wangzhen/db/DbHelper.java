package cn.zju.id21732091.wangzhen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wzlab on 2018/6/13.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME,null,StatusContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s int primary key,%s text, %s text, %s int, %s text)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.MESSAGE,
                StatusContract.Column.CREATED_AT,
                StatusContract.Column.IMG);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + StatusContract.TABLE);
        onCreate(db);
    }
}

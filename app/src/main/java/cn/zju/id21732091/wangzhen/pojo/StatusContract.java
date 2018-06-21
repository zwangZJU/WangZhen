package cn.zju.id21732091.wangzhen.pojo;

import android.provider.BaseColumns;

/**
 * Created by wzlab on 2018/6/13.
 */

public class StatusContract {
    public static final String DB_NAME = "timeline.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "status";
    public static final String DEFAULT_SORT = Column.CREATED_AT + "DESC";

    public static final String NEW_STATUSES = "cn.zju.id21732091.wangzhen.NEW_STATUSES";

    public class Column{
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
        public static final String IMG = "img";
    }
}

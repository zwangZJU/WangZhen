package cn.zju.id21732091.wangzhen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.zju.id21732091.wangzhen.service.UpdateService;

/**
 * Created by wzlab on 2018/6/27.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UpdateService.class));

    }
}

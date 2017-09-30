package com.tzdq.bluetooth.ble.base;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by csf on 2017/6/5.
 * 文件描述  长连接的设备服务，用startService进行连接,如果需要与界面进行交互，可以同时使用bindService
 */

public abstract class BaseLongRunService extends BaseCommonService {
    private String address = null;//地址
    private String TAG = this.getClass().getSimpleName();
    private MyBroadCastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        startRunThread();
        IntentFilter intentFilter = new IntentFilter(BluetoothBroadCastFlag.BROADCAST_ALL_STOP_LONG_RUN_FLAG);
        receiver = new MyBroadCastReceiver();
        this.registerReceiver(receiver, intentFilter);

        //状态栏保活
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    private void startRunThread() {
        Log.i(TAG, "开启检测断开连接的线程");
        runOnHandlerDelayed(runnable,7000);
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothBroadCastFlag.BROADCAST_ALL_STOP_LONG_RUN_FLAG)) {
                stopService();
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String tempAddress = getAddress();
            if (!isConnect() && tempAddress != null && !tempAddress.equals("")) {//如果当前未连接，并且有绑定地址
                if (address == null || !address.equals(tempAddress))
                    address = getAddress();
                if (!TextUtils.isEmpty(address)) {
                    Log.i(TAG, "开始扫描重连");
                    connectDevice(address);
                }
            }
            Log.i(TAG, "正在检测断开连接,当前链接状态" + isConnect() + ",地址+" + address + "保存的地址:" + tempAddress);
            runOnHandlerDelayed(runnable, 7000);
        }
    };

    @Override
    public void onDestroy() {
        handlerRemoveRunnable(runnable);
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }
}

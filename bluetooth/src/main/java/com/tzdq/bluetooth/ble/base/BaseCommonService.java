package com.tzdq.bluetooth.ble.base;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by csf on 2017/5/25.
 * 文件描述 所有设备的共有基类
 * 调用方式是bind();
 */

public abstract class BaseCommonService extends BaseBluetoothService {

    private String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        //        public void
        public BaseCommonService getService() {
            return BaseCommonService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

}

package com.tzdq.bluetooth.context;

import android.content.Context;

/**
 * 文件描述：
 * Created by csf on 2017/5/18.
 */
public class BlueToothContext {
    private static BlueToothContext instance;
    private Context context;

    private BlueToothContext() {

    }

    public static BlueToothContext getInstance() {
        if (instance == null) {
            instance = new BlueToothContext();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context.getApplicationContext();
    }
}

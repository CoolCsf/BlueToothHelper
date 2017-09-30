package com.tzdq.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

import com.tzdq.bluetooth.context.BlueToothContext;

/**
 * 文件描述：
 * Created by csf on 2017/5/18.
 */

public class SharedPreferencesHelper {
    private static final String FILE_NAME = "sp_ble";
    private SharedPreferences sharedPreferences;
    private static SharedPreferencesHelper instance;

    private SharedPreferencesHelper() {
        sharedPreferences = BlueToothContext.getInstance().getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getInstance() {//使用protected修饰，禁止包外调用
        if (instance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (instance == null) {
                    instance = new SharedPreferencesHelper();
                }
            }
        }
        return instance;
    }

    public void save(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }

    public boolean delete(String key) {
        return sharedPreferences.edit().remove(key).commit();
    }
}

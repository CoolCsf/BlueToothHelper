package com.tzdq.bluetooth.ble.base;

import android.content.Intent;
import android.os.Parcelable;

import com.tzdq.bluetooth.SharedPreferencesHelper;
import com.tzdq.bluetooth.context.BlueToothContext;

/**
 * 文件描述：蓝牙工具类，此类不向外部module开放
 * 外部module若需获取蓝牙相关方法，请移步BlueToothHelper类
 * Created by csf on 2017/5/18.
 */

public class BlueToothUtil {
    private static BlueToothUtil instance;


    private BlueToothUtil() {

    }

    public static BlueToothUtil getInstance() {
        if (instance == null) {
            synchronized (BlueToothUtil.class) {
                if (instance == null) {
                    instance = new BlueToothUtil();
                }
            }
        }
        return instance;
    }

    public boolean isCurrentDevice(String deviceName, String name) {
        if (deviceName.equals(name))
            return true;
        if (deviceName.substring(0, 2).equals(name))
            return true;
        return false;
    }

    public void sendBroadCast(String action) {
        Intent intent = new Intent(action);
        BlueToothContext.getInstance().getContext().sendBroadcast(intent);
    }

    public void sendBroadCast(String action, Parcelable entity) {
        Intent intent = new Intent(action);
        if (entity != null)
            intent.putExtra(action, entity);
        BlueToothContext.getInstance().getContext().sendBroadcast(intent);
    }

    public String getDeviceAddress(String deviceSign) {
        return SharedPreferencesHelper.getInstance().get(deviceSign);
    }

    public void saveDeviceAddress(String deviceSign, String address) {
        SharedPreferencesHelper.getInstance().save(deviceSign, address);
    }

    public void deleteDeviceAddress(String deviceSign) {
        SharedPreferencesHelper.getInstance().delete(deviceSign);
    }
}

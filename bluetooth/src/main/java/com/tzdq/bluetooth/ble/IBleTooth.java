package com.tzdq.bluetooth.ble;

/**
 * Created by csf on 2017/6/7.
 * 文件描述
 */

public interface IBleTooth {
    boolean isConnect();

    <T> boolean init(T model);

    void stopService();

    String getAddress();

    void saveAddress(String address);

    void deleteAddress();

    void scanDevice();

    boolean connectDevice(String address);

    void scanAndConnect(String address);

    void disConnect();

    void stopScan();

    String getDataSign();
}

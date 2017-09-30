package com.tzdq.bluetooth.ble.base;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.tzdq.bluetooth.DataUtil;
import com.tzdq.bluetooth.SharedPreferencesHelper;
import com.tzdq.bluetooth.ble.IBleTooth;
import com.tzdq.bluetooth.modle.DeviceModel;
import com.tzdq.bluetooth.modle.ScanDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 文件描述：
 * Created by csf on 2017/5/17.
 */

public abstract class BaseBluetoothService extends Service implements IBleTooth {
    private BluetoothManager mBluetoothManager;//蓝牙管理者
    private BluetoothAdapter mBluetoothAdapter; //蓝牙适配器
    private final int DELAY_TIME = 15000;
    private String TAG = this.getClass().getSimpleName();
    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> scanTempList;//临时存储扫描的设备列表
    protected DeviceModel deviceModel;//定义为protected，子类可随时修改此模型
    private BluetoothGatt bluetoothGatt;//设备Gatt
    private BluetoothGattService mGattService;//蓝牙设备Gatt服务
    private BluetoothGattCharacteristic writeCharacter;
    private int writeDataLength = 40;//单次写入的数据长度,默认40
    private boolean connect = false;
    private MyHandler handler;
    private Runnable leScanRunnable;
    private String address = "";

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";//打开独特征配置的UUID

    @Override
    public void stopService() {
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        stopLeScan();
        handler = null;
        disConnect();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        deviceModel = getDeviceModel();//缓存设备模型
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    /**
     * 扫描
     */
    private synchronized void scanLeDevice() {// 扫描---------
        /**
         * 判断蓝牙是否已打开
         */
        if (scanTempList == null)
            scanTempList = new ArrayList<>();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.stopLeScan(leScanCallback);//先停止扫描
            mBluetoothAdapter.startLeScan(leScanCallback);//开始扫描
            BlueToothUtil.getInstance().sendBroadCast(BluetoothBroadCastFlag.BROADCAST_START_SCAN_FLAG);
            /**
             * 扫描15秒后若没连接则自动停止，没有这部分代码若没连接到则会自动停止
             */
            if (leScanRunnable == null) {
                leScanRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        stopLeScan();
                    }
                };
            }
            runOnHandlerDelayed(leScanRunnable, DELAY_TIME);
        }
    }

    private void stopLeScan() {
        mBluetoothAdapter.stopLeScan(leScanCallback);
        BlueToothUtil.getInstance().sendBroadCast(BluetoothBroadCastFlag.BROADCAST_STOP_SCAN_FLAG);
        if (scanTempList != null && scanTempList.size() > 0)
            scanTempList.clear();//扫描结束后，清除临时列表缓存
        if (leScanRunnable != null) {//停止扫描
            handlerRemoveRunnable(leScanRunnable);
            leScanRunnable = null;
        }
        Log.i(TAG, "停止扫描");
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getAddress() != null && device.getName() != null && device.getName().length() >= 2) {
                Log.i(TAG, "扫描到的地址是" + device.getAddress());
                if (!address.equals("") && address.equals(device.getAddress()) && !connect) {//有绑定该设备,并且没有链接
                    //连接该设备
                    connect(device.getAddress());
                } else {//没有绑定该设备
                    if (isCurrentDevice(device) == null) {//如果子类没有重写判断是否为当前设备的方法
                        if (BlueToothUtil.getInstance().isCurrentDevice(device.getName(), deviceModel.getDeviceName()) &&
                                !deviceModel.getDeviceSign().equals("")) {//根据名字判断是否是当前设备
                            sendScan(device.getAddress(), deviceModel.getDeviceSign());
                        }
                    } else if (isCurrentDevice(device) && !deviceModel.getDeviceSign().equals("")) {//如果子类有重写判断是否为当前设备的方法，且扫描的设备是当前设备
                        sendScan(device.getAddress(), deviceModel.getDeviceSign());
                    }
                }
            }
        }
    };

    private void sendScan(String address, String name) {
        if (!scanTempList.contains(address)) {//如果还没发送广播过该地址
            ScanDataModel model = new ScanDataModel();
            model.setDeviceAddress(address);
            model.setDeviceName(name);//以设备的标记位作为Name发送给需要接收扫描的页面
            sendData(BluetoothBroadCastFlag.BROADCAST_SCAN_DATA_FLAG, model);
            scanTempList.add(address);
            Log.i(TAG, "添加元素成功");
        }
    }

    private synchronized boolean connect(String address) {// 连接
        Log.i(TAG, "链接设备:" + address);
        BluetoothDevice localBluetoothDevice = mBluetoothAdapter
                .getRemoteDevice(address);  //根据地址创建一个BluetoothDevice类实例（蓝牙设备）
        if (null != localBluetoothDevice) {
            if (bluetoothGatt != null) {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
            bluetoothGatt = localBluetoothDevice.connectGatt(this, false,
                    myGattCallback);//注册特征服务回调，连接
            if (bluetoothGatt != null) {
                Log.i(TAG, "链接成功");
                return true;
            }
        }
        return false;
    }


    private BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {// 连接回调函数
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接
                gatt.discoverServices();//获取远程服务,进入onServicesDiscovered回调
                connect = true;
                Log.i(TAG, "连接成功");
                BlueToothUtil.getInstance().sendBroadCast(deviceModel.getDeviceSign() + BluetoothBroadCastFlag.BROADCAST_CONNECTED_FLAG);//发送已连接的广播
                stopLeScan();//停止扫描
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//断开连接
                connect = false;
                if (bluetoothGatt != null)
                    bluetoothGatt.close();
                bluetoothGatt = null;
                Log.i(TAG, "断开链接");
                BlueToothUtil.getInstance().sendBroadCast(deviceModel.getDeviceSign() + BluetoothBroadCastFlag.BROADCAST_DISCONNECT_FLAG);//发送断开连接的广播
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {// 发现服务回调--------------
            super.onServicesDiscovered(gatt, status);
            if (bluetoothGatt == gatt) {
                mGattService = gatt.getService(UUID.fromString(deviceModel.getDeviceDataUUID()));
                if (null != mGattService) {
                    BluetoothGattCharacteristic readCharacter = mGattService.getCharacteristic(UUID.fromString(deviceModel.getDeviceReadUUID()));
                    if (!deviceModel.getDeviceWriteUUID().equals("")) {
                        writeCharacter = mGattService.getCharacteristic(UUID.fromString(deviceModel.getDeviceWriteUUID()));
                    }
                    if (null != readCharacter) {
                        setCharacterNotification(readCharacter, gatt);
                    }
                    if (null != readCharacter || null != writeCharacter) {//当有读或者写特征时
                        initDeviceAction();//初始化设备的动作，如发送指令给设备
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {// 数据接收-------------
            Log.i(TAG, "onCharacteristicChanged");
            if (characteristic.getValue() != null && characteristic.getValue().length > 0) {
                Parcelable value = getData(characteristic.getValue());
                if (null != value) {
                    Log.i(TAG, "发送数据");
                    BlueToothUtil.getInstance().sendBroadCast(deviceModel.getDeviceSign(), value);
                }
            }
        }

        // 发送消息结果回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {// 信号

        }
    };

    private void setCharacterNotification(BluetoothGattCharacteristic characteristic,
                                          BluetoothGatt gatt) {
        //必须开启回调通知，才可以开启硬件的config回调Enable。
        gatt.setCharacteristicNotification(characteristic, true);//标记是否开启特征改变时的通知，true时，则会进入onCharacteristicChanged回调
        //设配部分手机
        gatt.readCharacteristic(characteristic);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
//        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * 写入数据，如果数据长度超过限制的长度，则利用递归进行裁剪
     *
     * @param data
     */
    protected void writeStringToGatt(String data) {
        try {
            if (data != null) {
                Log.i(TAG, "发送指令" + data);
                if (data.length() <= writeDataLength) {
                    byte[] dataByte = DataUtil.getBytesByString(data);
                    if (null == writeCharacter) {
                        writeCharacter = mGattService.getCharacteristic(UUID.fromString(deviceModel.getDeviceWriteUUID()));
                    }
                    if (null != writeCharacter && mBluetoothAdapter.isEnabled()) {
                        writeCharacter.setValue(dataByte);
                        Log.i(TAG, "开始写入指令" + data);
                        bluetoothGatt.writeCharacteristic(writeCharacter);//写入数据。
                    }
                } else {
                    writeStringToGatt(data.substring(0, writeDataLength));
                    if (data.length() > writeDataLength)
                        writeStringToGatt(data.substring(writeDataLength, data.length()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "写入数据失败" + e.toString());
        }
    }

    public void saveAddress(String address) {
        BlueToothUtil.getInstance().saveDeviceAddress(deviceModel.getDeviceSign(), address);
    }

    public void disConnect() {
        if (mBluetoothAdapter != null && bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            if (bluetoothGatt != null)
                bluetoothGatt.close();
            connect = false;
            Log.i(TAG, "断开连接");
            bluetoothGatt = null;
            BlueToothUtil.getInstance().sendBroadCast(deviceModel.getDeviceSign() + BluetoothBroadCastFlag.BROADCAST_DISCONNECT_FLAG);
        }
    }

    public boolean connectDevice() {
        return connect(BlueToothUtil.getInstance().getDeviceAddress(deviceModel.getDeviceSign()));
    }

    public boolean connectDevice(String address) {
        if (address != null && !address.equals("") && !connect)
            return connect(address);
        return false;
    }

    public <T> boolean init(T model) {
        return deviceInit(model);
    }

    /**
     * 各个设备初始化的方法
     *
     * @param model 各个设备不同的初始化模型
     * @param <T>   泛型
     * @return 是否初始化成功的标志
     */
    protected <T> boolean deviceInit(T model) {
        return false;
    }

    protected Boolean isCurrentDevice(BluetoothDevice device) {
        return null;
    }

    public String getDeviceName() {
        return deviceModel.getDeviceName();
    }

    public String getDeviceSign() {
        return deviceModel.getDeviceSign();
    }

    public void closeConnect() {

    }

    public void deleteAddress() {
        BlueToothUtil.getInstance().deleteDeviceAddress(deviceModel.getDeviceSign());
    }

    public boolean isConnect() {
        return connect;
    }

    public String getAddress() {
        return BlueToothUtil.getInstance().getDeviceAddress(deviceModel.getDeviceSign());
    }

    public void scanDevice() {
        scanLeDevice();
    }

    protected abstract void initDeviceAction();//开启设备的动作，不需要的可以不重写此方法

    //每个设备必须实现的方法。初始化设备的模型，供后续使用
    protected abstract DeviceModel getDeviceModel();

    protected abstract <T extends Parcelable> T getData(byte[] data);

    protected <T extends Parcelable> void sendData(String key, T entity) {
        BlueToothUtil.getInstance().sendBroadCast(key, entity);//发送数据
    }

    protected void runOnHandlerDelayed(Runnable runnable, long time) {
        if (handler == null) {
            handler = new MyHandler();
        }
        handler.postDelayed(runnable, time);
    }

    protected void handlerRemoveRunnable(Runnable runnable) {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    public String getDataSign() {
        return deviceModel.getDeviceSign();
    }

    public void scanAndConnect(String address) {
        this.address = address;
        scanDevice();
    }

    private static class MyHandler extends Handler {
    }

    @Override
    public void stopScan() {
        stopLeScan();
    }

    protected void setWriteDataLength(int length) {
        writeDataLength = length;
    }
}

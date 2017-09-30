package com.tzdq.bluetooth.ble.base;

/**
 * Created by csf on 2017/8/1.
 * 文件描述 蓝牙广播标识
 */

public class BluetoothBroadCastFlag {

    /**
     * 扫描数据标识
     */
    public static final String BROADCAST_SCAN_DATA_FLAG = "scan_data";
    /**
     * 扫描开始标识
     */
    public static final String BROADCAST_START_SCAN_FLAG = "start_scan";
    /**
     * 扫描结束标识
     */
    public static final String BROADCAST_STOP_SCAN_FLAG = "stop_scan";
    /**
     * 断开链接标识
     */
    public static final String BROADCAST_DISCONNECT_FLAG = "disConnect";//断开连接字段，为区别不同设备，发送时，在前面加上设备的sign。
    /**
     * 链接标识
     */
    public static final String BROADCAST_CONNECTED_FLAG = "connect";//连接字段，为区别不同设备，发送时，在前面加上设备的sign。
    /**
     * 停止所有长连接服务的广播标识
     */
    public static final String BROADCAST_ALL_STOP_LONG_RUN_FLAG = "stop_all_long_run";
    /**
     * 手环添加或者删除一条提醒的标识
     */
    public static final String BROADCAST_WRISTBAND_ADD_DELETE_SETTING_FLAG = "wristband_add_delete";
    /**
     * 手环没有设置记录的标识
     */
    public static final String BROADCAST_WRISTBAND_NOT_SETTING_FLAG = "wristband_not_setting";
}

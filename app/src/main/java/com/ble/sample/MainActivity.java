package com.ble.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tzdq.bluetooth.ble.IBleTooth;

public class MainActivity extends AppCompatActivity {
    //    private BlueToothFactory blueToothFactory;
    private IBleTooth bleTooth;
    private MyBroadCastReceiver receiver;
    private String TAG = this.getClass().getSimpleName();
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BlueToothContext.getInstance().setContext(this);
//        blueToothFactory = new BlueToothFactory();
//        bleTooth = blueToothFactory.getDevice(this, BleToothHelper.WRIST_BAND_SIGN);
//        receiver = new MyBroadCastReceiver();
//        IntentFilter filter = new IntentFilter(BleToothHelper.WRIST_BAND_SIGN);//为每个页面都自动注册取消连接的广播
//        registerReceiver(receiver, filter);
//        IntentFilter filter3 = new IntentFilter(BluetoothBroadCastFlag.BROADCAST_START_SCAN_FLAG);
//        registerReceiver(receiver, filter3);
//        IntentFilter filter1 = new IntentFilter(BluetoothBroadCastFlag.BROADCAST_STOP_SCAN_FLAG);
//        registerReceiver(receiver, filter1);
//        IntentFilter filter2 = new IntentFilter(BluetoothBroadCastFlag.BROADCAST_SCAN_DATA_FLAG);
//        registerReceiver(receiver, filter2);
//        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (address!=null)
//                    bleTooth.connectDevice(address);
//            }
//        });
//        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bleTooth.scanDevice();
//            }
//        });
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(BleToothHelper.WRIST_BAND_SIGN)) {
//                Bundle bundle = intent.getExtras();
//                if (bundle == null)
//                    return;
//                WristbandDataEntity shouHuanDataEntity = bundle.getParcelable(intent.getAction());
//                if (shouHuanDataEntity != null && (Wristband.DATA_SLEEP.equals(shouHuanDataEntity.getSign())
//                        || Wristband.DATA_REAL_TIME.equals(shouHuanDataEntity.getSign()))) {
////                    stepData = shouHuanDataEntity.getRealTimeStep();
////                    calorieData = shouHuanDataEntity.getRealTimeCalorie();
////                    mileageData = shouHuanDataEntity.getRealTimeMileage();
////                    if (shouHuanDataEntity.getSleepTime() != 0)
////                        sleepData = shouHuanDataEntity.getSleepTime();
////                    hearthRateData = shouHuanDataEntity.getRealTimeRate();
//                }
////                sleepData = ;
//            }
//            if (intent.getAction().equals(BluetoothBroadCastFlag.BROADCAST_SCAN_DATA_FLAG)) {
//                ScanDataModel dataModel = intent.getExtras().getParcelable(BluetoothBroadCastFlag.BROADCAST_SCAN_DATA_FLAG);
//                if (dataModel == null)
//                    return;
////                devices.add(dataModel.getDeviceAddress());
////                devicesss.put(dataModel.getDeviceAddress(), dataModel.getDeviceName());
//                address = dataModel.getDeviceAddress();
//            }
//            if (intent.getAction().equals(BluetoothBroadCastFlag.BROADCAST_STOP_SCAN_FLAG)) {
//                Log.i(TAG, "未发现设备");
//            }
//        }
//    }
        }
    }
}

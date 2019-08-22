package ru.alphanix.cleansoft.model.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class mBatInfoReceiver extends BroadcastReceiver {
    int temp = 0;

    public float get_temp(){
        return (float)(temp / 10);
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {
        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
    }
}

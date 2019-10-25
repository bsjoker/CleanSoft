package ru.alphanix.cleansoft.mainMenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.Utils.RAM_Memory;
import ru.alphanix.cleansoft.Utils.ROM_Memory;

public class MainMenuActivityPresenter {
    private final static String TAG = "MainMenuPresenter";

    private MainMenuActivity activity;

    private Date currentDate, currentDatePlus4Hour;
    private int temp = 0;
    IntentFilter intentfilter;
    float batteryTemp;

    public void setActivity(MainMenuActivity mainMenuActivity) {
        this.activity = mainMenuActivity;
        getCpuTemp();
        setPercentRAM(new RAM_Memory(activity));
        setPercentROM(new ROM_Memory(activity));
    }

    public void checkTimeDelayAfterClear() {
        currentDate = new Date();
        currentDatePlus4Hour = new Date();
        Log.d(TAG, "CurTime: " + currentDate.getHours());
        currentDate = Calendar.getInstance().getTime();
        currentDatePlus4Hour.setTime(PreferencesHelper.getSharedPreferences().
                getLong("currentDatePlus4Hour", 0));
        Log.d(TAG, "TomorrowTime: " + currentDatePlus4Hour.getHours());
        Long raznica = currentDatePlus4Hour.getTime() - currentDate.getTime();
        Log.d(TAG, "Разница: " + raznica);
        if (raznica <= 0) {
            try {
                PreferencesHelper.savePreference("boost", true);
                PreferencesHelper.savePreference("cool", true);
                PreferencesHelper.savePreference("cache", true);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        setStatusCheck();

        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        activity.registerReceiver(broadcastreceiver, intentfilter);
    }

    private void setStatusCheck() {
        ArrayList<Integer> numOfBlink = new ArrayList<>();
        numOfBlink.add(1);
        numOfBlink.add(2);
        numOfBlink.add(3);
        if (!PreferencesHelper.getSharedPreferences().getBoolean("boost", true)) {
            activity.switchOffBoost();
            numOfBlink.remove(0);
            //numOfBlink.trimToSize();
            Log.d(TAG, "Size: " + numOfBlink.size());
        }
        if (!PreferencesHelper.getSharedPreferences().getBoolean("cache", true)) {
            activity.switchOffClear();
            numOfBlink.remove(numOfBlink.size() - 2);
            Log.d(TAG, "Size: " + numOfBlink.size());
        }
        if (!PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
            activity.switchOffCool();
            numOfBlink.remove(numOfBlink.size() - 1);
            Log.d(TAG, "Size: " + numOfBlink.size());
        }


        if (!numOfBlink.isEmpty()) {
            activity.setBlink(numOfBlink.get(0));
        }
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            batteryTemp = (float) (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            if (temp == 0) {
                temp = Math.round(batteryTemp);
                setTempCPU(temp);
                Log.d(TAG, "Temp in receiver: " + temp);
            }
        }
    };

    public void getCpuTemp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Process p;
            try {
                p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                float temp = Float.parseFloat(line);
                this.temp = Math.round(temp);
            } catch (Exception e) {
                e.printStackTrace();
                temp = Math.round(0.0f);
            }
            setTempCPU(temp);
            Log.d(TAG, "Temp in getCpuTemp: " + temp);
        } else {
            temp = Math.round(batteryTemp);
            setTempCPU(temp);
            Log.d(TAG, "Temp in receiver: " + temp);

        }
    }

    private void setTempCPU(int t) {
        Log.d(TAG, "Temp: " + t);
        while (t > 100) {
            t = t / 10;
        }
        try {
            PreferencesHelper.savePreference("curTemp", t);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        if (t > 60) {
            activity.showProgress(activity.pbVerticalCPUred, 1500, 2000, t);
            activity.showProgress(activity.pbVerticalCPUgreen, 0, 3500, t - (t - 60));
        } else {
            activity.showProgress(activity.pbVerticalCPUred, 0, 3500, t);
            activity.showProgress(activity.pbVerticalCPUgreen, 0, 3500, t);
        }
    }

    private void setPercentRAM(RAM_Memory ram_memory) {
        Integer percentRAM = ram_memory.getPercentRAMVal();
        if (percentRAM > 50) {
            activity.showProgress(activity.pbVerticalRAMred, 1500, 2000, percentRAM);
            activity.showProgress(activity.pbVerticalRAMgreen, 0, 3500,
                    percentRAM - (percentRAM - 50));
        } else {
            activity.showProgress(activity.pbVerticalRAMred, 0, 3500, percentRAM);
            activity.showProgress(activity.pbVerticalRAMgreen, 0, 3500, percentRAM);
        }
    }

    private void setPercentROM(ROM_Memory rom_memory) {
        Integer percentStorage = rom_memory.getPercentStorageVal();

        if (percentStorage > 80) {
            activity.showProgress(activity.pbVerticalROMred, 1500, 2000, percentStorage);
            activity.showProgress(activity.pbVerticalROMgreen, 0, 3500,
                    percentStorage - (percentStorage - 50));
        } else {
            activity.showProgress(activity.pbVerticalROMgreen, 0, 3500, percentStorage);
            activity.showProgress(activity.pbVerticalROMred, 0, 3500, percentStorage);
        }
    }

    public void endAnimation(int maxValue, TextView type) {
        int padding_in_px = getScreenDestiny(maxValue);
        String value;
        switch (type.getId()) {
            case R.id.percentCPU:
                if (PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
                    if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)) {
                        DecimalFormat df = new DecimalFormat("####0.00");
                        double maxValueF = maxValue * 1.8 + 32;
                        value = df.format(maxValueF) + activity.getResources().getString(R.string.degreeceF);
                    } else {
                        value = maxValue + activity.getResources().getString(R.string.degreece);
                    }
                    activity.setTextOnGraph(type, value, padding_in_px);
                }
                break;
            case R.id.percentROM:
                if (PreferencesHelper.getSharedPreferences().getBoolean("cache", true)) {
                    value = maxValue + "%";
                    activity.setTextOnGraph(type, value, padding_in_px);
                }
                break;
            case R.id.percentRAM:
                if (PreferencesHelper.getSharedPreferences().getBoolean("boost", true)) {
                    value = maxValue + "%";
                    activity.setTextOnGraph(type, value, padding_in_px);
                }
                break;
        }
    }

    private int getScreenDestiny(int maxValue) {
        int padding_in_dp = (int) (maxValue * 2.5) - 50;
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    public void checkReadyForAds() {
        if(!PreferencesHelper.getSharedPreferences().getBoolean("boost", true)
                && !PreferencesHelper.getSharedPreferences().getBoolean("cache", true)
                && !PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
            activity.refreshAd();
        }
    }
}

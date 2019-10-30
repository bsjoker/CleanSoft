package ru.alphanix.cleansoft.cooling;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.Utils.AppsListHelper;

public class TempActivityPresenter {
    private final static String TAG = "TempActivityPresenter";

    private AppsListHelper appsListHelper;
    private TempActivity activity;
    private Context context;
    private int temp = 0;

    public TempActivityPresenter(Context context) {
        this.context = context;
        Log.d(TAG, "Presenter created!");
        appsListHelper = new AppsListHelper(context, "");
    }

    public void setActivity(TempActivity tempActivity) {
        this.activity = tempActivity;
        Log.d(TAG, "SetActivity!");
        getCpuTemp();
        activity.fillListApps(appsListHelper.getAppNames(), appsListHelper.getPackageNames());
    }

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
            temp = PreferencesHelper.getSharedPreferences().getInt("curTemp", 40);
            setTempCPU(temp);
            Log.d(TAG, "Temp in receiver: " + temp);

        }
    }

    private void setTempCPU(int t) {
        Log.d(TAG, "Temp: " + t);
        while (t > 100) {
            t = t / 10;
        }

        if (t>60) {
            activity.showProgress(activity.pbHorizontalRAMred, 1500, 2000, t);
            activity.showProgress(activity.pbHorizontalRAMgreen, 0, 3500, t-20);
        } else {
            activity.showProgress(activity.pbHorizontalRAMred, 0, 3500, t);
            activity.showProgress(activity.pbHorizontalRAMgreen, 0, 3500, t);
        }
    }

    public void killApps(ArrayList<String> packageNamesForKills) {
        appsListHelper.killAppsFromList(packageNamesForKills);
    }

    public void endAnimation(int maxValue) {
        int padding_in_px = getScreenDestiny(maxValue);
        String value;
        if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)){
            double tF = maxValue*1.8 + 32;
            DecimalFormat df = new DecimalFormat("####0.00");
            value = df.format(tF) + context.getResources().getString(R.string.degreeceF);
        } else {
            value = maxValue + context.getResources().getString(R.string.degreece);
        }
        activity.setTextOnGraph(value, padding_in_px);
    }

    private int getScreenDestiny(int maxValue) {
        int padding_in_dp = (int) (maxValue * 2.5) - 50;
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }
}

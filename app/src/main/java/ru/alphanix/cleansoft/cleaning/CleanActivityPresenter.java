package ru.alphanix.cleansoft.cleaning;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

import ru.alphanix.cleansoft.Utils.AppsListHelper;
import ru.alphanix.cleansoft.Utils.ROM_Memory;

public class CleanActivityPresenter {
    private final static String TAG = "CleanActivityPresenter";
    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 123;
    private CleanCacheFakeActivity activity;
    private Context context;
    private AppsListHelper appsListHelper;
    private ROM_Memory rom_memory;

    public CleanActivityPresenter(Context context) {
        this.context = context;
        appsListHelper = new AppsListHelper(context);
    }

    public void setActivity(CleanCacheFakeActivity activity) {
        this.activity = activity;
        rom_memory = new ROM_Memory(activity);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
            Log.i(TAG, "Request!");
        } else {
            setPercentRAM(rom_memory);
            activity.fillListApps(appsListHelper.getAppNames(), appsListHelper.getPackageNames());
            activity.clearCache();
        }
    }

    public void onPermissionsResult(int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setPercentRAM(rom_memory);
            activity.fillListApps(appsListHelper.getAppNames(), appsListHelper.getPackageNames());
            activity.clearCache();
            Log.i(TAG, "Granted result WRITE_EXTERNAL_STORAGE!");
        } else {
            // permission denied
            Log.i(TAG, "Denied result WRITE_EXTERNAL_STORAGE!");
        }
    }

    public void endAnimation(int maxValue) {
        int padding_in_px = getScreenDestiny(maxValue);
        String value = maxValue + "%";
        activity.setTextOnGraph(value, rom_memory.getBusyAndFreeSpace(), padding_in_px);
    }

    private int getScreenDestiny(int maxValue) {
        int padding_in_dp = (int) (maxValue*2.5) - 60;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    private void setPercentRAM(ROM_Memory rom_memory) {
        Integer percentStorage = rom_memory.getPercentStorageVal();
        if (percentStorage>80) {
            activity.showProgress(activity.pbHorizontalROMred, 1500, 2000, percentStorage);
            activity.showProgress(activity.pbHorizontalROMgreen, 0, 3500, percentStorage-20);
        } else {
            activity.showProgress(activity.pbHorizontalROMred, 0, 3500, percentStorage);
            activity.showProgress(activity.pbHorizontalROMgreen, 0, 3500, percentStorage);
        }
    }

    public void killApps(ArrayList<String> packageNamesForKills) {
        appsListHelper.killAppsFromList(packageNamesForKills);
    }
}

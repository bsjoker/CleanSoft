package ru.alphanix.cleansoft.boosting;

import android.content.Context;

import java.util.ArrayList;

import ru.alphanix.cleansoft.Utils.AppsListHelper;
import ru.alphanix.cleansoft.Utils.RAM_Memory;

class BoostActivityPresenter {
    private final static String TAG = "BoostActivityPresenter";
    private Context context;
    BoostActivity activity;

    private AppsListHelper appsListHelper;
    private RAM_Memory ram_memory;

    public BoostActivityPresenter(Context context) {
        this.context = context;
        appsListHelper = new AppsListHelper(context, "");
    }

    public void setActivity(BoostActivity boostActivity) {
        this.activity = boostActivity;
        ram_memory = new RAM_Memory(activity);
        setPercentRAM(ram_memory);
        activity.fillListApps(appsListHelper.getAppNames(), appsListHelper.getPackageNames());
    }

    public void endAnimation(int maxValue) {
        int padding_in_px = getScreenDestiny(maxValue);
        String value = maxValue + "%";
        activity.setTextOnGraph(value, ram_memory.getBusyAndFreeSpace(), padding_in_px);
    }

    private int getScreenDestiny(int maxValue) {
        int padding_in_dp = (int) (maxValue*2.5) - 60;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    private void setPercentRAM(RAM_Memory ram_memory) {
        Integer percentRAM = ram_memory.getPercentRAMVal();
        if (percentRAM>50) {
            activity.showProgress(activity.pbHorizontalRAMred, 1500, 2000, percentRAM);
            activity.showProgress(activity.pbHorizontalRAMgreen, 0, 3500, percentRAM - (percentRAM-50));
        } else {
            activity.showProgress(activity.pbHorizontalRAMred, 0, 3500, percentRAM);
            activity.showProgress(activity.pbHorizontalRAMgreen, 0, 3500, percentRAM);
        }
    }

    public void killApps(ArrayList<String> packageNamesForKills) {
        appsListHelper.killAppsFromList(packageNamesForKills);
    }
}

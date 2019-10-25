package ru.alphanix.cleansoft.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AppsListHelper {
    private Context context;
    private ActivityManager am;
    private List<ApplicationInfo> packagesRun;
    private ArrayList<String> packageNames = new ArrayList<>();
    private ArrayList<String> appNames = new ArrayList<>();

    public AppsListHelper(Context context) {
        this.context = context;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        fillListApps();
    }

    private void fillListApps() {
        PackageManager pm = context.getPackageManager();
        packagesRun = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packagesRun) {
            boolean system = (packageInfo.flags & packageInfo.FLAG_SYSTEM) > 0;
            boolean stoped = (packageInfo.flags & packageInfo.FLAG_STOPPED) > 0;
            //system apps! get out
            if (!stoped && !system) {

                packageNames.add(packageInfo.packageName);
                appNames.add(packageInfo.loadLabel(pm).toString());
                //am.killBackgroundProcesses(packageInfo.packageName);
            }
        }
    }

    public void killAppsFromList(ArrayList<String> packageNamesForKills){
        for (String packageName : packageNamesForKills){
            am.killBackgroundProcesses(packageName);
            Log.d("AppsListHelper", packageName + " killed!");
        }
    }

    public ArrayList<String> getPackageNames() {
        return packageNames;
    }

    public ArrayList<String> getAppNames() {
        return appNames;
    }
}

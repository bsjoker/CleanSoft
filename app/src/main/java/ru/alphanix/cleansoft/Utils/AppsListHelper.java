package ru.alphanix.cleansoft.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.R;

public class AppsListHelper {
    private final static String TAG = "AppsListHelper";
    private Context context;
    private ActivityManager am;
    private List<ApplicationInfo> packagesRun;
    private ArrayList<String> packageNames = new ArrayList<>();
    private ArrayList<String> appNames = new ArrayList<>();

    public AppsListHelper(Context context, String typeList) {
        this.context = context;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        switch (typeList) {
            case "cache":
                fillListAppsWithCache();
                break;
            default:
                fillListApps();
                break;
        }

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
            }
        }
    }

    private void fillListAppsWithCache() {
        PackageManager pm = context.getPackageManager();
        packagesRun = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packagesRun) {
                long size = checkCache(packageInfo.packageName);
                if(size>0) {
                    packageNames.add(packageInfo.packageName);
                    String sizeStr = String.format("%.2f", (double)size/1000000);
                    appNames.add(packageInfo.loadLabel(pm) + " (" + sizeStr + " " + context.getApplicationContext().getResources().getString(R.string.mb) + ")");
            }
        }
    }

    private long checkCache(String packageName) {
        long size = 0;
        Context applicationContext = App.getInstance().getApplicationContext();
        File externalCacheDir = applicationContext.getExternalCacheDir();
        if (externalCacheDir != null) {
            File file = new File(externalCacheDir.getAbsolutePath().replace(applicationContext.getPackageName(), packageName));
            if (file.exists() && file.isDirectory()) {
                size = getDirSize(file);
                Log.d(TAG, "111:: " + getDirSize(file) + packageName);
            }
        }
        return size;
    }

    private long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public void killAppsFromList(ArrayList<String> packageNamesForKills) {
        for (String packageName : packageNamesForKills) {
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

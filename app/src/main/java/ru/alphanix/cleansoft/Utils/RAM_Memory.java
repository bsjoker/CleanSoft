package ru.alphanix.cleansoft.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;

import java.io.File;

public class RAM_Memory {
    private Long percentRAMVal;
    private ActivityManager.MemoryInfo mi;
    private AppCompatActivity activity;

    public RAM_Memory(AppCompatActivity activity) {
        this.activity = activity;
        ActivityManager am = (ActivityManager) activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //sizeRAM.setText(Formatter.formatFileSize(getApplicationContext(), mi.totalMem - mi.availMem) + " " + getResources().getString(R.string.from) + " " + Formatter.formatFileSize(getApplicationContext(), mi.totalMem));
        percentRAMVal = Math.round(((mi.totalMem - mi.availMem) / 10737418.24) / (mi.totalMem / 1073741824.0));

        File r = new File(Environment.getDataDirectory().getAbsolutePath());
        long total = r.getTotalSpace();
        long usage = total - r.getFreeSpace();
    }

    public String getBusyAndFreeSpace(){
        return Formatter.formatFileSize(activity.getApplicationContext(), mi.totalMem - mi.availMem) + " / " + Formatter.formatFileSize(activity.getApplicationContext(), mi.totalMem);
    }

    public int getPercentRAMVal() {
        return percentRAMVal.intValue();
    }
}

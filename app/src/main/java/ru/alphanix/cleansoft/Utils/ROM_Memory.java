package ru.alphanix.cleansoft.Utils;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;

import java.io.File;

public class ROM_Memory {
    private Long percentStorageVal, total, usage;
    private AppCompatActivity activity;

    public ROM_Memory(AppCompatActivity activity) {
        this.activity = activity;
        File r = new File(Environment.getDataDirectory().getAbsolutePath());
        total = r.getTotalSpace();
        usage = total - r.getFreeSpace();
        //sizeStorage.setText(Formatter.formatFileSize(getApplicationContext(), usage) + " " + getResources().getString(R.string.from) + " " + Formatter.formatFileSize(getApplicationContext(), total));
        percentStorageVal = Math.round((usage / 10737418.24) / (total / 1073741824.0));
    }

    public String getBusyAndFreeSpace(){
        return Formatter.formatFileSize(activity.getApplicationContext(), usage) + " / " + Formatter.formatFileSize(activity.getApplicationContext(), total);
    }

    public int getPercentStorageVal() {
        return percentStorageVal.intValue();
    }
}

package ru.alphanix.cleansoft.process;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.InvalidClassException;
import java.util.Calendar;
import java.util.Date;

import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.widget.Sendmail;

public class ProcessActivityPresenter {
    public static final String TAG = "ProcessPresenter";

    private ProcessActivity activity;
    private Context context;
    private Date currentDatePlus4Hour;
    private String mTitle, mFinishText, process = " ";
    private int count;
    private Sendmail mSendmail;

    public ProcessActivityPresenter(Context context) {
        this.context = context;
    }

    public void setActivity(ProcessActivity activity) {
        this.activity = activity;
        currentDatePlus4Hour = new Date();
        checkRateCount();
        whatIsTheProcess();
        activity.showProgress();
        checkSplitAds();
    }

    private void checkRateCount() {
        count = PreferencesHelper.getSharedPreferences().getInt("countStarts1", 0);
        Log.i("TAG", "Count: " + count);
        if (count == 3 && !PreferencesHelper.getSharedPreferences().getBoolean("isDeepLink", false)) {
            activity.showAlertDialog();
            try {
                PreferencesHelper.savePreference("countStarts1", count + 1);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }
    }

    private void whatIsTheProcess() {
        process = activity.getIntent().getStringExtra("process");

        Log.d("Proc", process);
        switch (process) {
            case "boost":
                mTitle = context.getResources().getString(R.string.ram);
                mFinishText = context.getResources().getString(R.string.memory_boosted);
                break;
            case "cool":
                mTitle = context.getResources().getString(R.string.cpu);
                mFinishText = context.getResources().getString(R.string.system_cooled);
                break;
            case "cache":
                mTitle = context.getResources().getString(R.string.rom);
                mFinishText = context.getResources().getString(R.string.system_cleared);
                clearCache(activity.getIntent().getStringArrayExtra("packagesForCleanCache"));
                break;
        }

        activity.setTitleAndFinishText(mTitle, mFinishText);

        try {
            PreferencesHelper.savePreference(process, false);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    private void checkSplitAds() {
        if(PreferencesHelper.getSharedPreferences().getBoolean("isSplitNative", true)){
            activity.refreshAd();
            try {
                PreferencesHelper.savePreference("isSplitNative", false);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        } else {
            activity.showAdMobAds();
            try {
                PreferencesHelper.savePreference("isSplitNative", true);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        showAnimation();
    }

    private void showAnimation() {
        activity.startAnimation(activity.mAnimCircle1, 360f, 2000, 0);
        activity.startAnimation(activity.mAnimCircle2, -360f, 1800, 200);
        activity.startAnimation(activity.mAnimCircle3, 360f, 1800, 200);
        activity.startAnimation(activity.mAnimCircle4, -360f, 2000, 0);
    }

    public void clickBackMenu() {
        // Устанавливает время на 5 минут вперед
        Calendar mCalendar1 = Calendar.getInstance();
        mCalendar1.setTimeInMillis(currentDatePlus4Hour.getTime());
        //mCalendar1.add(Calendar.HOUR_OF_DAY, 4);
        mCalendar1.add(Calendar.MINUTE, 5);
        Log.d(TAG, "Time: " + mCalendar1);
        currentDatePlus4Hour.setTime(mCalendar1.getTimeInMillis());
        try {
            PreferencesHelper.savePreference("currentDatePlus4Hour", mCalendar1.getTimeInMillis());
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    public void sendMail(EditText mEtName, EditText mEtEmail, EditText mEtComment) {
        mSendmail = new Sendmail();

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mEtName.getText())) {
            sb.append("Name: " + mEtName.getText() + "\n");
        }
        if (!TextUtils.isEmpty(mEtEmail.getText())) {
            sb.append("Email: " + mEtEmail.getText() + "\n");
        }
        if (!TextUtils.isEmpty(mEtComment.getText())) {
            sb.append("Comment: " + mEtComment.getText());
        }

        mSendmail.sendMail(activity, sb);
        Log.d("TAG", "Positive btn - send mail!");
    }

    private void clearCache(String[] appsForCleanCache) {
        Context applicationContext = App.getInstance().getApplicationContext();
        File externalCacheDir = applicationContext.getExternalCacheDir();
        if (externalCacheDir != null) {
            for (String appForCleanCache: appsForCleanCache) {
                File file = new File(externalCacheDir.getAbsolutePath().replace(applicationContext.getPackageName(), appForCleanCache));
                if (file.exists() && file.isDirectory()) {
                    Log.d(TAG, "111:: " + appForCleanCache);
                    deleteFile(file);
                }
            }
        }
    }

    private static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            if (list != null) {
                for (String file2 : list) {
                    if (!deleteFile(new File(file, file2))) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}

package ru.alphanix.cleansoft.setting;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.io.InvalidClassException;
import java.util.Locale;

import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.Utils.PreferencesHelper;

public class SettingActivityPresenter {
    public static final String TAG = "SettingPresenter";
    private SettingActivity activity;
    private Context context;
    Locale locale, defLocale;;
    Configuration configuration;

    public SettingActivityPresenter(Context context) {
        this.context = context;
    }


    public void setActivity(SettingActivity activity) {
        this.activity = activity;
    }

    public void clickChange() {
        if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)){
            try {
                PreferencesHelper.savePreference("isFahrenheit", false);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreferencesHelper.savePreference("isFahrenheit", true);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }
    }


    public void setLocale(String[] items, int item, Context context) {
        try {
            PreferencesHelper.savePreference("lang", items[item]);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        String[] itemsLocale = context.getResources().getStringArray(R.array.langLocaleArray);
        changeLocale(itemsLocale[item], context);
    }

    public void changeLocale(String lang, Context context) {
//        defLocale = context.getResources().getConfiguration().locale;
//        Locale locale = new Locale(PreferencesHelper.getSharedPreferences().getString("locale", defLocale.getCountry()));
//        Locale.setDefault(locale);
//        Configuration configuration = new Configuration();
//        configuration.setLocale(locale);
//        context.getResources().updateConfiguration(configuration,
//                context.getResources().getDisplayMetrics());

//        defLocale = activity.getBaseContext().getResources().getConfiguration().locale;
//        locale = new Locale(PreferencesHelper.getSharedPreferences().getString("locale", defLocale.getCountry()));
//        Locale.setDefault(locale);
//        configuration = new Configuration();
//        configuration.setLocale(locale);
//        activity.getBaseContext().getResources().updateConfiguration(configuration, null);
        if (lang != "na") {
            try {
                PreferencesHelper.savePreference("locale", lang);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        Log.d("SettingPresenter", "Lang: " + lang);
        //activity.setLocale(lang);
    }
}

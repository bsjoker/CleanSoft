package ru.alphanix.cleansoft.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "locale";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        return PreferencesHelper.getSharedPreferences().getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    //----------------------------------------------------------------------------------------------

//    public static Context onAttach(Context context) {
//        String locale = getPersistedLocale();
//        return setLocale(context, locale);
//    }
//
//    public static String getPersistedLocale() {
//        return PreferencesHelper.getSharedPreferences().getString("locale", "en");
//    }
//
//    /**
//     * Set the app's locale to the one specified by the given String.
//     *
//     * @param context
//     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
//     *                   support country and variant codes so far); the special string "system" sets
//     *                   the locale to the locale specified in system settings
//     * @return
//     */
//    public static Context setLocale(Context context, String localeSpec) {
//        Locale locale;
//        if (localeSpec.equals("system")) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
//            } else {
//                //noinspection deprecation
//                locale = Resources.getSystem().getConfiguration().locale;
//            }
//        } else {
//            locale = new Locale(localeSpec);
//        }
//        Locale.setDefault(locale);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return updateResources(context, locale);
//        } else {
//            return updateResourcesLegacy(context, locale);
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.N)
//    private static Context updateResources(Context context, Locale locale) {
//        Configuration configuration = context.getResources().getConfiguration();
//        configuration.setLocale(locale);
//        configuration.setLayoutDirection(locale);
//
//        return context.createConfigurationContext(configuration);
//    }
//
//    @SuppressWarnings("deprecation")
//    private static Context updateResourcesLegacy(Context context, Locale locale) {
//        Resources resources = context.getResources();
//
//        Configuration configuration = resources.getConfiguration();
//        configuration.locale = locale;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLayoutDirection(locale);
//        }
//
//        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//
//        return context;
//    }
}
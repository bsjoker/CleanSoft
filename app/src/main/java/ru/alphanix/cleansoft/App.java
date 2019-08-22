package ru.alphanix.cleansoft;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.json.JSONObject;

import java.io.InvalidClassException;
import java.util.Locale;

/**
 * Created by adm1n on 04.05.2017.
 */

public class App extends Application {
    private String API_key = "df3acd5b-15ac-4e93-98c7-fab0f1521c29";
    private static App appInstance;

    public static App getInstance(){
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //Создание расширенной конфигурации библиотеки.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking(this);

        appInstance = this;

        try {
            PreferencesHelper.savePreference("isDeepLink", false);
            Log.i("OneSignalExample", "customkey set with value: " + PreferencesHelper.getSharedPreferences().getBoolean("isDeepLink", false));
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;

            if (data != null) {
                customKey = data.optString("key", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // Этот блок вызывается при нажатии на уведомление.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            JSONObject data = result.notification.payload.additionalData;

            String customKey;
            Object activityToLaunch = LoadActivity.class;

            if (data != null) {
                customKey = data.optString("key", null);

                if (customKey != null)
                    switch (customKey) {
                        case "mainscreen":
                            activityToLaunch = MainMenuActivity.class;
                            break;
                        case "boostmscreen":
                            activityToLaunch = BoostActivity.class;
                            break;
                    }
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
                try {
                    PreferencesHelper.savePreference("isDeepLink", true);
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}

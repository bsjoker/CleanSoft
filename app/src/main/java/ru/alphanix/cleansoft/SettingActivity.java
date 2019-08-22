package ru.alphanix.cleansoft;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InvalidClassException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingActivity extends AppCompatActivity{
    private final static String TAG = "SettingActivity";
    @BindView(R.id.curDegrees)
    TextView mCurDegrees;

    @BindView(R.id.targetDegrees)
    TextView mTargetDegrees;

    @BindView(R.id.tvTitle)
    TextView tvTitleMenu;

    @BindView(R.id.tvLangVal)
    TextView tvLang;

    @BindView(R.id.includeSetting)
    Toolbar mActionBarToolbar;

    Locale locale;
    Configuration configuration;
    private String mTempDegrees;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mUnbinder = ButterKnife.bind(this);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        tvTitleMenu.setText(getResources().getString(R.string.setting));
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvLang.setText(PreferencesHelper.getSharedPreferences().getString("lang", getResources().getString(R.string.english)));

        if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)) {
            mCurDegrees.setText(R.string.fahrenheit);
            mTargetDegrees.setText(R.string.celsius);
        }

        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MenuBack", "Back clicked");
                finish();
                startActivity(new Intent(SettingActivity.this, MenuActivity.class));
            }
        });
    }

    public void onClickChange(View view) throws InvalidClassException {
        mTempDegrees = (String) mCurDegrees.getText();
        mCurDegrees.setText(mTargetDegrees.getText());
        mTargetDegrees.setText(mTempDegrees);
        if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)){
            PreferencesHelper.savePreference("isFahrenheit", false);
        } else {
            PreferencesHelper.savePreference("isFahrenheit", true);
        }
    }

    public void onClickLang(View view) {
        final String[] items = getResources().getStringArray(R.array.langArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.lang));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.i(TAG, "Item: " + item);
                tvLang.setText(items[item]);
                try {
                    PreferencesHelper.savePreference("lang", items[item]);
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                switch (item){
                    case 0:
                        locale = new Locale("en");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("en");
                        break;
                    case 1:
                        locale = new Locale("es");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("es");
                        break;
                    case 2:
                        locale = new Locale("pt");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("pt");
                        break;
                    case 3:
                        locale = new Locale("ja");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("ja");
                        break;
                    case 4:
                        locale = new Locale("th");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("th");
                        break;
                    case 5:
                        locale = new Locale("in");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("in");
                        break;
                    case 6:
                        locale = new Locale("fr");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("fr");
                        break;
                    case 7:
                        locale = new Locale("zh");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("zh");
                        break;
                    case 8:
                        locale = new Locale("tr");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("tr");
                        break;
                    case 9:
                        locale = new Locale("ro");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("ro");
                        break;
                    case 10:
                        locale = new Locale("pl");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("pl");
                        break;
                    case 11:
                        locale = new Locale("ms");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("ms");
                        break;
                    case 12:
                        locale = new Locale("ko");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("ko");
                        break;
                    case 13:
                        locale = new Locale("vi");
                        Locale.setDefault(locale);
                        configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration, null);
                        saveLang("vi");
                        break;
                }
                finish();
                startActivity(new Intent(SettingActivity.this, SettingActivity.class));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveLang(String lang) {
        try {
            PreferencesHelper.savePreference("locale", lang);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }
}

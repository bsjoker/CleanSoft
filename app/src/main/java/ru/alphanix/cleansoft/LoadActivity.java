package ru.alphanix.cleansoft;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.InvalidClassException;
import java.util.Locale;

import butterknife.BindView;

public class LoadActivity extends AppCompatActivity {
    Locale locale, defLocale;
    Configuration configuration;
    private FirebaseAnalytics mFirebaseAnalytics;

    private ImageView wave, wave2, wave2d, wave3, wave4, wave4d;
    private TextView tvBoost, tvClean, tvOptimize, tvPrivacy;
    private int count;
    private boolean isStart;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        defLocale = getApplicationContext().getResources().getConfiguration().locale;
        defLocale.getCountry();
        Log.i("LoadActivity", defLocale.getCountry());

        locale = new Locale(PreferencesHelper.getSharedPreferences().getString("locale", defLocale.getCountry()));
        Locale.setDefault(locale);
        configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        changeLocale(locale);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intersentialIDfirst));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        count = PreferencesHelper.getSharedPreferences().getInt("countStarts1", 0);

        if (count < 4) {
            try {
                PreferencesHelper.savePreference("countStarts1", count + 1);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        wave = findViewById(R.id.ivWave);
        wave2 = findViewById(R.id.ivWave2);
        wave2d = findViewById(R.id.ivWave2double);
        wave3 = findViewById(R.id.ivWave3);
        wave4 = findViewById(R.id.ivWave4);
        wave4d = findViewById(R.id.ivWave4double);

        Typeface tf = Typeface.createFromAsset(getAssets(), "roboto.ttf");

        tvBoost = findViewById(R.id.tvBoost);
        tvClean = findViewById(R.id.tvClean);
        tvOptimize = findViewById(R.id.tvOptimize);
        tvPrivacy = findViewById(R.id.tvPrivacy);

        tvBoost.setTypeface(tf);
        tvClean.setTypeface(tf);
        tvOptimize.setTypeface(tf);
        tvPrivacy.setTypeface(tf);

        startAnimationWave(wave, 2000f, 4500);
        startAnimationWave(wave2, 5500f, 4500);
        startAnimationWave(wave2d, 5500f, 4500);
        startAnimationWave(wave3, -2000f, 4500);
        startAnimationWave(wave4, -5500f, 4500);
        startAnimationWave(wave4d, -5500f, 4500);

        startAnimationWord(tvBoost, 500);
        startAnimationWord(tvClean, 2500);
        startAnimationWord(tvOptimize, 4500);
    }

    private void startAnimationWord(final TextView mAnimWave, long timeDelay) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimWave, "translationY", 1000f, 0f);
        oa1.setStartDelay(timeDelay);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(2000).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mAnimWave.getId() == R.id.tvBoost) {
                    tvBoost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (tvClean.isShown()) {
                    tvOptimize.setVisibility(View.VISIBLE);
                } else {
                    tvClean.setVisibility(View.VISIBLE);
                }

                if (mAnimWave.equals(tvOptimize)) {
                    if (mInterstitialAd.isLoaded()) {
                        showNextActivity();
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                Toast.makeText(LoadActivity.this, "Ad Loaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                Toast.makeText(LoadActivity.this, "Ad Failed To Load", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdClosed() {

                            }
                        });
                    } else {
                        showNextActivity();
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }
            }
        });
    }

    public void showNextActivity() {
        if (!isStart) {
            startActivity(new Intent(LoadActivity.this, MainMenuActivity.class));
            finish();
        }
    }

    private void startAnimationWave(ImageView mAnimWave, float endPos, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(mAnimWave, "translationX", 0f, endPos);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.setDuration(duration).start();
    }

    @SuppressWarnings("deprecation")
    private void changeLocale(Locale locale) {
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources()
                .updateConfiguration(configuration,
                        getBaseContext()
                                .getResources()
                                .getDisplayMetrics());
        setTitle(R.string.app_name);

        tvBoost = findViewById(R.id.tvBoost);
        tvClean = findViewById(R.id.tvClean);
        tvOptimize = findViewById(R.id.tvOptimize);

        tvBoost.setText(R.string.boost);
        tvClean.setText(R.string.clean);
        tvOptimize.setText(R.string.optimize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locale = new Locale(PreferencesHelper.getSharedPreferences().getString("locale", defLocale.getCountry()));
        Locale.setDefault(locale);
        configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    public void onClickPolicy(View view) {
        startActivity(new Intent(LoadActivity.this, PolicyFirstActivity.class));
        isStart = true;
        finish();
    }
}

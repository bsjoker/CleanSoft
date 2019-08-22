package ru.alphanix.cleansoft;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.ads.consent.*;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.model.adapter.mBatInfoReceiver;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainMenuActivity";
    private static long back_pressed;

    @BindView(R.id.pb_verticalCPUred)
    ProgressBar pbVerticalCPUred;

    @BindView(R.id.pb_verticalCPUgreen)
    ProgressBar pbVerticalCPUgreen;

    @BindView(R.id.percentCPU)
    TextView tvPercentCPU;

    @BindView(R.id.pb_verticalROMred)
    ProgressBar pbVerticalROMred;

    @BindView(R.id.pb_verticalROMgreen)
    ProgressBar pbVerticalROMgreen;

    @BindView(R.id.percentROM)
    TextView tvPercentROM;

    @BindView(R.id.pb_verticalRAMred)
    ProgressBar pbVerticalRAMred;

    @BindView(R.id.pb_verticalRAMgreen)
    ProgressBar pbVerticalRAMgreen;

    @BindView(R.id.percentRAM)
    TextView tvPercentRAM;

    @BindView(R.id.clCool)
    ConstraintLayout mclCool;

    @BindView(R.id.clClear)
    ConstraintLayout mclClear;

    @BindView(R.id.clBoost)
    ConstraintLayout mclBoost;

    @BindView(R.id.ivStrokeCool)
    ImageView ivStrokeCool;

    @BindView(R.id.ivStrokeClear)
    ImageView ivStrokeClear;

    @BindView(R.id.ivStrokeBoost)
    ImageView ivStrokeBoost;

    @BindView(R.id.clCoolGraph)
    ConstraintLayout mclCoolGraph;

    @BindView(R.id.clClearGraph)
    ConstraintLayout mclClearGraph;

    @BindView(R.id.clBoostGraph)
    ConstraintLayout mclBoostGraph;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    @BindView(R.id.scrollView2)
    ScrollView mNativeScrollView;

    @BindView(R.id.ivCheckCool)
    ImageView ivCheckCool;

    @BindView(R.id.ivCheckClear)
    ImageView ivCheckClear;

    @BindView(R.id.ivCheckBoost)
    ImageView ivCheckBoost;

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;

    private ConsentForm form;
    private Unbinder mUnbinder;
    private int count = 0, t = 0;
    private boolean isBlink;
    float tempC;
    String fileName, line = null;
    private AdView mAdView;

    private mBatInfoReceiver myBatInfoReceiver;
    IntentFilter intentfilter;
    float batteryTemp;
    private Date currentDate, currentDatePlus4Hour;
    private AnimatorSet set;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        mUnbinder = ButterKnife.bind(this);

        nativeAd = new NativeAd(this, "486963001835819_537412883457497");
        //nativeAd = new NativeAd(this, "VID_HD_16_9_46S_LINK#486963001835819_537412883457497");
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG, "Native Error!");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
//                inflateAd(nativeAd);
                Log.i(TAG, "Native loaded!");
                //mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);

        currentDate = new Date();
        currentDatePlus4Hour = new Date();
        Log.d(TAG, "CurTime: " + currentDate.getHours());
        currentDate = Calendar.getInstance().getTime();
        currentDatePlus4Hour.setTime(PreferencesHelper.getSharedPreferences().getLong("currentDatePlus4Hour", 0));
        Log.d(TAG, "TomorrowTime: " + currentDatePlus4Hour.getHours());
        Long raznica = currentDatePlus4Hour.getTime() - currentDate.getTime();
        Log.d(TAG, "Разница: " + raznica);
        if (raznica <= 0) {
            try {
                PreferencesHelper.savePreference("boost", true);
                PreferencesHelper.savePreference("cool", true);
                PreferencesHelper.savePreference("cache", true);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_blink);

        if (PreferencesHelper.getSharedPreferences().getBoolean("boost", true)
                && PreferencesHelper.getSharedPreferences().getBoolean("cache", true)
                && PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
            mclCool.setOnClickListener(this);
            mclClear.setOnClickListener(this);
            mclBoost.setOnClickListener(this);
            mclCoolGraph.setOnClickListener(this);
            mclClearGraph.setOnClickListener(this);
            mclBoostGraph.setOnClickListener(this);
            set.setTarget(ivStrokeBoost);
            isBlink = true;
        } else {
            if (PreferencesHelper.getSharedPreferences().getBoolean("boost", true)) {
                mclBoost.setOnClickListener(this);
                mclBoostGraph.setOnClickListener(this);
                set.setTarget(ivStrokeBoost);
                isBlink = true;
            } else {
                mclBoost.setAlpha(0.5f);
                ivCheckBoost.setAlpha(0.0f);
                startAnimationCheck(ivCheckBoost, 0);
                ivStrokeBoost.setVisibility(View.GONE);
                pbVerticalRAMgreen.setAlpha(0.0f);
                pbVerticalRAMred.setVisibility(View.GONE);
                tvPercentRAM.setVisibility(View.GONE);
                count++;
            }
            if (PreferencesHelper.getSharedPreferences().getBoolean("cache", true)) {
                mclClear.setOnClickListener(this);
                mclClearGraph.setOnClickListener(this);
                if(!isBlink) {
                    set.setTarget(ivStrokeClear);
                    isBlink = true;
                }
            } else {
                mclClear.setAlpha(0.5f);
                ivCheckClear.setAlpha(0.0f);
                startAnimationCheck(ivCheckClear, 0);
                ivStrokeClear.setVisibility(View.GONE);
                pbVerticalROMgreen.setAlpha(0.0f);
                pbVerticalROMred.setVisibility(View.GONE);
                tvPercentROM.setVisibility(View.GONE);
                count++;
            }
            if (PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
                mclCool.setOnClickListener(this);
                mclCoolGraph.setOnClickListener(this);
                if(!isBlink) {
                    set.setTarget(ivStrokeCool);
                }
            } else {
                mclCool.setAlpha(0.5f);
                ivCheckCool.setAlpha(0.0f);
                startAnimationCheck(ivCheckCool, 0);
                ivStrokeCool.setVisibility(View.GONE);
                pbVerticalCPUgreen.setVisibility(View.GONE);
                pbVerticalCPUred.setVisibility(View.GONE);
                tvPercentCPU.setVisibility(View.GONE);
            }
//            if () {
//                mclBoost.setAlpha(0.5f);
//                mclClear.setAlpha(0.5f);
//                mclCool.setAlpha(0.5f);
//
//                //mNativeScrollView.setVisibility(View.VISIBLE);
//                ivCheckCool.setAlpha(0.0f);
//                ivCheckClear.setAlpha(0.0f);
//                ivCheckBoost.setAlpha(0.0f);
//                startAnimationCheck(ivCheckBoost, 0);
//                startAnimationCheck(ivCheckClear, 500);
//                startAnimationCheck(ivCheckCool, 1000);
//                ivStrokeBoost.setVisibility(View.GONE);
//                pbVerticalRAMgreen.setVisibility(View.GONE);
//                pbVerticalRAMred.setVisibility(View.GONE);
//                tvPercentRAM.setVisibility(View.GONE);
//                ivStrokeClear.setVisibility(View.GONE);
//                pbVerticalROMgreen.setVisibility(View.GONE);
//                pbVerticalROMred.setVisibility(View.GONE);
//                tvPercentROM.setVisibility(View.GONE);
//                ivStrokeCool.setVisibility(View.GONE);
//                pbVerticalCPUgreen.setVisibility(View.GONE);
//                pbVerticalCPUred.setVisibility(View.GONE);
//                tvPercentCPU.setVisibility(View.GONE);
//            }
        }

        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        final ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        String[] publisherIds = {"pub-2215717436233572"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                boolean inEEA = ConsentInformation.getInstance(getApplicationContext()).isRequestLocationInEeaOrUnknown();

                if (inEEA) {
                    Toast.makeText(MainMenuActivity.this, consentStatus.toString(), Toast.LENGTH_LONG).show();
                    if (consentStatus == ConsentStatus.PERSONALIZED) {

                    } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");

                        AdRequest request = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    } else {
                        URL privacyUrl = null;
                        try {
                            // TODO: Replace with your app's privacy policy URL.
                            privacyUrl = new URL("http://mobileprosafe.com/policy/privacy_policy_RO.html");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            // Handle error.
                        }
                        form = new ConsentForm.Builder(MainMenuActivity.this, privacyUrl)
                                .withListener(new ConsentFormListener() {
                                    @Override
                                    public void onConsentFormLoaded() {
                                        form.show();
                                    }

                                    @Override
                                    public void onConsentFormOpened() {
                                        // Consent form was displayed.
                                    }

                                    @Override
                                    public void onConsentFormClosed(
                                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                        // Consent form was closed.
                                        if (consentStatus == consentStatus.NON_PERSONALIZED) {
                                            Bundle extras = new Bundle();
                                            extras.putString("npa", "1");

                                            AdRequest request = new AdRequest.Builder()
                                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                    .build();
                                        }
                                    }

                                    @Override
                                    public void onConsentFormError(String errorDescription) {
                                        // Consent form error.
                                    }
                                })
                                .withPersonalizedAdsOption()
                                .withNonPersonalizedAdsOption()
                                //.withAdFreeOption()
                                .build();
                        form.load();
                    }
                } else {

                }

            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

        myBatInfoReceiver = new mBatInfoReceiver();

        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        MainMenuActivity.this.registerReceiver(broadcastreceiver, intentfilter);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.include);
//        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
//        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });


        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(getResources().getString(R.string.app_name));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            t = Math.round(getCpuTemp());
            startTempCPU(t);
        }

        Integer percentRAM = (int) calculateRAMMemory();
        Integer percentStorage = (int) calculateStorageFree();

        if (percentStorage > 80) {
            showProgress(pbVerticalROMred, 1500, 2000, percentStorage);
            showProgress(pbVerticalROMgreen, 0, 3500, percentStorage - (percentStorage - 50));
        } else {
            showProgress(pbVerticalROMgreen, 0, 3500, percentStorage);
            showProgress(pbVerticalROMred, 0, 3500, percentStorage);
        }

        if (percentRAM > 50) {
            showProgress(pbVerticalRAMred, 1500, 2000, percentRAM);
            showProgress(pbVerticalRAMgreen, 0, 3500, percentRAM - (percentRAM - 50));
        } else {
            showProgress(pbVerticalRAMred, 0, 3500, percentRAM);
            showProgress(pbVerticalRAMgreen, 0, 3500, percentRAM);
        }
    }

    private void startAnimationCheck(final ImageView mAnimWave, long timeDelay) {
        switch (count){
            case 1:
                timeDelay = timeDelay + 500;
                break;
            case 2:
                timeDelay = timeDelay + 1000;
                break;
        }
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimWave, "translationY", 300f, 0f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAnimWave, "alpha", 0.0f, 1.0f);
        oa1.setStartDelay(timeDelay);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(2000).start();
        oa2.setStartDelay(timeDelay);
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa2.setDuration(2000).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mAnimWave.getId() == R.id.ivCheckBoost) {
                    ivCheckBoost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ivCheckClear.isShown()) {
                    ivCheckCool.setVisibility(View.VISIBLE);
                } else {
                    ivCheckClear.setVisibility(View.VISIBLE);
                }
                //if (mAnimWave.getId() == R.id.ivCheckCool && ivCheckClear.isShown() && ivCheckBoost.isShown()) {
                if (ivStrokeBoost.getVisibility() == View.GONE && ivStrokeClear.getVisibility() == View.GONE && ivStrokeCool.getVisibility() == View.GONE) {
                    mNativeScrollView.setVisibility(View.VISIBLE);
                    inflateAd(nativeAd);
                }
            }
        });
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container_main);
        LayoutInflater inflater = LayoutInflater.from(MainMenuActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainMenuActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    private void startTempCPU(int t) {

        //Toast.makeText(MainMenuActivity.this, "T before = " + t, Toast.LENGTH_SHORT).show();
        while (t > 100) {
            t = t / 10;
        }

        try {
            PreferencesHelper.savePreference("curTemp", t);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
        //Toast.makeText(MainMenuActivity.this, "T after = " + t, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainMenuActivity.this, "Count = " + count, Toast.LENGTH_SHORT).show();

        if (t > 60) {
            showProgress(pbVerticalCPUred, 1500, 2000, t);
            showProgress(pbVerticalCPUgreen, 0, 3500, t - (t - 60));
        } else {
            showProgress(pbVerticalCPUred, 0, 3500, t);
            showProgress(pbVerticalCPUgreen, 0, 3500, t);
        }
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            batteryTemp = (float) (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            //Log.i(TAG, "Temp test receiver");
            //Log.i(TAG, "Temp receiver" + batteryTemp);

            if (t == 0) {

                t = Math.round(batteryTemp);
                startTempCPU(t);
            }
        }
    };

    private void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()) {
            case R.id.pb_verticalCPUred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (PreferencesHelper.getSharedPreferences().getBoolean("cool", true)) {
                            if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)) {
                                DecimalFormat df = new DecimalFormat("####0.00");
                                double maxValueF = maxValue * 1.8 + 32;
                                String valueF = String.valueOf(df.format(maxValueF));
                                if (valueF != null && !valueF.isEmpty()) {
                                    try {
                                        tvPercentCPU.setText(valueF + getResources().getString(R.string.degreeceF));
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                String value = String.valueOf(maxValue);
                                if (value != null && !value.isEmpty()) {
                                    try {

                                        tvPercentCPU.setText(value + getResources().getString(R.string.degreece));

                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            int padding_in_dp = (int) (maxValue * 2.5) - 50;
                            final float scale = getResources().getDisplayMetrics().density;
                            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                            try {
                                tvPercentCPU.setPadding(0, 0, 0, padding_in_px);
                                tvPercentCPU.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;

            case R.id.pb_verticalROMred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (PreferencesHelper.getSharedPreferences().getBoolean("cache", true)) {
                            String valueROM = String.valueOf(maxValue);
                            if (valueROM != null && !valueROM.isEmpty()) {
                                try {
                                    tvPercentROM.setText(valueROM + "%");
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }


                            int padding_in_dp = (int) (maxValue * 2.5) - 50;
                            final float scale = getResources().getDisplayMetrics().density;
                            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                            try {
                                tvPercentROM.setPadding(0, 0, 0, padding_in_px);
                                tvPercentROM.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;

            case R.id.pb_verticalRAMred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (PreferencesHelper.getSharedPreferences().getBoolean("boost", true)) {
                            String valueRAM = String.valueOf(maxValue);
                            if (valueRAM != null && !valueRAM.isEmpty()) {
                                try {
                                    tvPercentRAM.setText(valueRAM + "%");
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            int padding_in_dp = (int) (maxValue * 2.5) - 50;
                            final float scale = getResources().getDisplayMetrics().density;
                            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                            try {
                                tvPercentRAM.setPadding(0, 0, 0, padding_in_px);
                                tvPercentRAM.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;
        }
        animation.setStartDelay(delay);
        animation.setDuration(duration); // 3.5 second
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    private long calculateRAMMemory() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //sizeRAM.setText(Formatter.formatFileSize(getApplicationContext(), mi.totalMem - mi.availMem) + " " + getResources().getString(R.string.from) + " " + Formatter.formatFileSize(getApplicationContext(), mi.totalMem));
        Long percentRAMVal = Math.round(((mi.totalMem - mi.availMem) / 10737418.24) / (mi.totalMem / 1073741824.0));

        File r = new File(Environment.getDataDirectory().getAbsolutePath());
        long total = r.getTotalSpace();
        long usage = total - r.getFreeSpace();

        showProgress(pbVerticalRAMgreen, 0, 3500, percentRAMVal.intValue());
        return percentRAMVal;
    }

    private long calculateStorageFree() {
        File r = new File(Environment.getDataDirectory().getAbsolutePath());
        long total = r.getTotalSpace();
        long usage = total - r.getFreeSpace();
        //sizeStorage.setText(Formatter.formatFileSize(getApplicationContext(), usage) + " " + getResources().getString(R.string.from) + " " + Formatter.formatFileSize(getApplicationContext(), total));
        long percentStorageVal = Math.round((usage / 10737418.24) / (total / 1073741824.0));
        return percentStorageVal;
    }

    public float getCpuTemp() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();
            float temp = Float.parseFloat(line);

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu) {
            startActivity(new Intent(MainMenuActivity.this, MenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clCool:
                startActivity(new Intent(MainMenuActivity.this, TempActivity.class));
                break;
            case R.id.clClear:
                startActivity(new Intent(MainMenuActivity.this, CleanCacheFakeActivity.class));
                break;
            case R.id.clBoost:
                startActivity(new Intent(MainMenuActivity.this, BoostActivity.class));
                break;
            case R.id.clCoolGraph:
                startActivity(new Intent(MainMenuActivity.this, TempActivity.class));
                break;
            case R.id.clClearGraph:
                startActivity(new Intent(MainMenuActivity.this, CleanCacheFakeActivity.class));
                break;
            case R.id.clBoostGraph:
                startActivity(new Intent(MainMenuActivity.this, BoostActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.press_for_exit), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}

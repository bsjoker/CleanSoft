package ru.alphanix.cleansoft;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.widget.Sendmail;

public class ProcessActivityNew extends AppCompatActivity {
    @BindView(R.id.progress)
    ProgressBar pbCircle;

    @BindView(R.id.progressInner)
    ProgressBar pbCircleInner;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    @BindView(R.id.finishText)
    TextView tvFinishText;

    @BindView(R.id.clProcess)
    ConstraintLayout mClProcess;

    @BindView(R.id.ivLogoApp)
    ImageView ivLogoApp;

    @BindView(R.id.ivLogoAppBottom)
    ImageView ivLogoAppBottom;

    @BindView(R.id.ivOK)
    ImageView iv_ok;

    @BindView(R.id.tvNameApp)
    TextView tvNameApp;

    @BindView(R.id.llApps)
    LinearLayout llApps;

    @BindView(R.id.ivpbInner)
    ImageView mIvPbInner;

    ObjectAnimator oa;

    private InterstitialAd mInterstitialAd;

    private String[] appsArray;
    private PackageManager pm;
    private EditText mEtComment, mEtName, mEtEmail;
    private int count, mPackagesForKills, i = 0;
    private int progress, speed;
    private AlertDialog dialog;
    String process = "dd";
    private Unbinder mUnbinder;
    private AdView mAdView;

    Sendmail mSendmail;
//    private NativeAdLayout nativeAdLayout;
//    private LinearLayout adView;
//    private NativeAd nativeAd;

    private Date currentDatePlus4Hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_new);

        mUnbinder = ButterKnife.bind(this);
        currentDatePlus4Hour = new Date();

        AudienceNetworkAds.initialize(this);

        showProgress();

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adViewRect);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intersentialIDSysOpt));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

//        nativeAd = new NativeAd(this, "486963001835819_506222603243192");

        Bundle b = this.getIntent().getExtras();
        appsArray = b.getStringArray("appsKey");
        for (int i = 0; i<appsArray.length; i++){
            Log.i("TAG", "Pack: " + appsArray[i]);
        }

        pm = getApplicationContext().getPackageManager();

        startIconAnimation(i);
        mPackagesForKills = getIntent().getIntExtra("packagesForKills", 0);

//        Log.d("Proc", process);
//        switch (process) {
//            case "boost":
//                tvTitle.setText(getResources().getString(R.string.ram));
//                tvFinishText.setText(getResources().getString(R.string.memory_boosted));
//                break;
//            case "cool":
//                tvTitle.setText(getResources().getString(R.string.cpu));
//                tvFinishText.setText(getResources().getString(R.string.system_cooled));
//                break;
//            case "cache":
//                tvTitle.setText(getResources().getString(R.string.rom));
//                tvFinishText.setText(getResources().getString(R.string.system_cleared));
//                break;
//        }
        tvTitle.setText(getResources().getString(R.string.sys_optimization));

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();

                // Устанавливает время на 4 часа вперед
                Calendar mCalendar1 = Calendar.getInstance();
                mCalendar1.setTimeInMillis(currentDatePlus4Hour.getTime());
                mCalendar1.add(Calendar.HOUR_OF_DAY, 4);
                currentDatePlus4Hour.setTime(mCalendar1.getTimeInMillis());
                try {
                    PreferencesHelper.savePreference("currentDatePlus4Hour", mCalendar1.getTimeInMillis());
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(ProcessActivityNew.this, MainMenuActivity.class));
                finish();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //llFinishOK.setVisibility(View.VISIBLE);
                //startAnimationOK(ivFinishOK, tvFinishOK);
            }

            @Override
            public void onAdClosed() {
                mAdView.setVisibility(View.GONE);
                ConstraintSet constraintSet = new ConstraintSet();
                ConstraintLayout constraintLayout = findViewById(R.id.lp);
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.llApps,ConstraintSet.BOTTOM,R.id.scrollView,ConstraintSet.TOP,8);
                constraintSet.connect(R.id.llApps,ConstraintSet.START,R.id.scrollView,ConstraintSet.START,0);
                constraintSet.connect(R.id.llApps,ConstraintSet.END,R.id.scrollView,ConstraintSet.END,0);
                constraintSet.applyTo(constraintLayout);
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) llApps.getLayoutParams();
//                params.bottomToTop = R.id.native_ad_container;
//                llApps.requestLayout();
//                nativeAd.setAdListener(new NativeAdListener() {
//                    @Override
//                    public void onMediaDownloaded(Ad ad) {
//
//                    }
//
//                    @Override
//                    public void onError(Ad ad, AdError adError) {
//                        Log.i("TAG", "Native Error!");
//                    }
//
//                    @Override
//                    public void onAdLoaded(Ad ad) {
//                        if (nativeAd == null || nativeAd != ad) {
//                            return;
//                        }
//                        // Inflate Native Ad into Container
//                        inflateAd(nativeAd);
//                        Log.i("TAG", "Native loaded!");
//                        //mAdView.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAdClicked(Ad ad) {
//
//                    }
//
//                    @Override
//                    public void onLoggingImpression(Ad ad) {
//
//                    }
//                });
//                nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
            }
        });
    }

//    private void inflateAd(NativeAd nativeAd) {
//
//        nativeAd.unregisterView();
//
//        // Add the Ad view into the ad container.
//        nativeAdLayout = findViewById(R.id.native_ad_container);
//        LayoutInflater inflater = LayoutInflater.from(ProcessActivityNew.this);
//        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
//        nativeAdLayout.addView(adView);
//
//        // Add the AdOptionsView
//        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(ProcessActivityNew.this, nativeAd, nativeAdLayout);
//        adChoicesContainer.removeAllViews();
//        adChoicesContainer.addView(adOptionsView, 0);
//
//        // Create native UI using the ad metadata.
//        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
//        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
//        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
//        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
//        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
//        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
//
//        // Set the Text.
//        nativeAdTitle.setText(nativeAd.getAdvertiserName());
//        nativeAdBody.setText(nativeAd.getAdBodyText());
//        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
//
//        // Create a list of clickable views
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(nativeAdTitle);
//        clickableViews.add(nativeAdCallToAction);
//
//        // Register the Title and CTA button to listen for clicks.
//        nativeAd.registerViewForInteraction(
//                adView,
//                nativeAdMedia,
//                nativeAdIcon,
//                clickableViews);
//    }

    private void startIconAnimation(int i) {
        Drawable icon = null;
        try {
            icon = pm.getApplicationIcon(appsArray[i]);
            ApplicationInfo ai = pm.getApplicationInfo(appsArray[i], PackageManager.GET_META_DATA);
            tvNameApp.setText(ai.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ivLogoApp.setImageDrawable(icon);
        ivLogoAppBottom.setImageDrawable(icon);
        startAnimationAppearance(ivLogoApp, llApps);
    }

    private void startAnimationAppearance(final ImageView mAnimLogo, final LinearLayout llApp) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimLogo, "translationX", 150f, 0f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAnimLogo, "alpha", 0f, 1f);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(llApp, "alpha", 0f, 1f);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(100).start();
        oa2.setDuration(100).start();
        oa3.setDuration(100).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startAnimationDisappearance(mAnimLogo, llApp);
            }
        });
    }

    private void startAnimationDisappearance(final ImageView mAnimLogo, final LinearLayout llApp) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimLogo, "translationX", 0f, -150f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAnimLogo, "alpha", 1f, 0f);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(llApp, "alpha", 1f, 0f);
        oa1.setStartDelay(500);
        oa2.setStartDelay(500);
        oa3.setStartDelay(500);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(100).start();
        oa2.setDuration(100).start();
        oa3.setDuration(100).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (i<appsArray.length - 1) {
                    i++;
                    startIconAnimation(i);
                } else {
                    ivLogoAppBottom.setImageDrawable(getResources().getDrawable(R.drawable.check));
                    ViewGroup.LayoutParams lp = ivLogoAppBottom.getLayoutParams();
                    lp.width = ivLogoAppBottom.getWidth()/2;
                    lp.height = ivLogoAppBottom.getHeight()/2;
                    ivLogoAppBottom.setLayoutParams(lp);
                    tvNameApp.setText(getResources().getString(R.string.done));
                    ObjectAnimator oaOKll = ObjectAnimator.ofFloat(llApp, "alpha", 0f, 1f);
                    ObjectAnimator oaOK = ObjectAnimator.ofFloat(iv_ok, "alpha", 0f, 1f);
                    oaOKll.setDuration(100).start();
                    oaOK.setDuration(100).start();
                    pbCircleInner.setVisibility(View.GONE);
                    mIvPbInner.setVisibility(View.VISIBLE);
                    oaOKll.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgress() {
        speed = 145+mPackagesForKills;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (progress = 0; progress < 100; progress++) {
                    try {
                        speed--;
                        Thread.sleep(speed);
                        pbCircle.setProgress(progress+1);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(count!=3) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            i = appsArray.length;
                        }
                    });
                }
            }
        }).
                start();
    }

    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickGO(View view) {
    }
}

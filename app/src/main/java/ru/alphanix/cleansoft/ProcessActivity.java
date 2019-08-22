package ru.alphanix.cleansoft;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.*;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;

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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ProcessActivity extends AppCompatActivity {
    public static final String TAG = "ProcessActivity";

    @BindView(R.id.pb_horizontal)
    ProgressBar pbHorizontal;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    @BindView(R.id.finishText)
    TextView tvFinishText;

    @BindView(R.id.tvProcess)
    TextView tvProcess;

//    @BindView(R.id.button2)
//    Button mButtonGo;

    @BindView(R.id.clProcess)
    ConstraintLayout mClProcess;

    @BindView(R.id.scrollView)
    ScrollView mNativeScrollView;

    ObjectAnimator oa;

    private InterstitialAd mInterstitialAd;

    private EditText mEtComment, mEtName, mEtEmail;
    private int count, mPackagesForKills;
    private int progress, speed;
    private AlertDialog dialog;
    String process = "dd";
    private Unbinder mUnbinder;
    private ImageView mAnimCircle1, mAnimCircle2, mAnimCircle3, mAnimCircle4;
    private AdView mAdView;
    private Bundle b;
    //private Boolean isNotLoadAds = false;

    Sendmail mSendmail;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;
    private Date currentDatePlus4Hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mUnbinder = ButterKnife.bind(this);

        currentDatePlus4Hour = new Date();

        AudienceNetworkAds.initialize(this);

        showProgress();

//        SdkConfiguration sdkConfiguration =
//                new SdkConfiguration.Builder("YOUR_MOPUB_AD_UNIT_ID").build();
//
//        MoPub.initializeSdk(getApplicationContext(), sdkConfiguration, null);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adViewRect);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intersentialID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        nativeAd = new NativeAd(this, "486963001835819_506222603243192");
        //nativeAd = new NativeAd(this, "VID_HD_16_9_46S_LINK#486963001835819_506222603243192");
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i("TAG", "Native Error!");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
                Log.i("TAG", "Native loaded!");
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

        b = this.getIntent().getExtras();

        count = PreferencesHelper.getSharedPreferences().getInt("countStarts1", 0);
        Log.i("TAG", "Count: " + count);
        if (count == 3 && !PreferencesHelper.getSharedPreferences().getBoolean("isDeepLink", false)) {
            showAlertDialog();
        }

        process = getIntent().getStringExtra("process");
        mPackagesForKills = getIntent().getIntExtra("packagesForKills", 0);

        Log.d("Proc", process);
        switch (process) {
            case "boost":
                tvTitle.setText(getResources().getString(R.string.ram));
                tvFinishText.setText(getResources().getString(R.string.memory_boosted));
                break;
            case "cool":
                tvTitle.setText(getResources().getString(R.string.cpu));
                tvFinishText.setText(getResources().getString(R.string.system_cooled));
                break;
            case "cache":
                tvTitle.setText(getResources().getString(R.string.rom));
                tvFinishText.setText(getResources().getString(R.string.system_cleared));
                break;
        }

        try {
            PreferencesHelper.savePreference(process, false);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();

                // Устанавливает время на 4 часа вперед
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
                startActivity(new Intent(ProcessActivity.this, MainMenuActivity.class).putExtra("temp", PreferencesHelper.getSharedPreferences().getInt("curTemp", 40) - r.nextInt(8)));
                finish();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAnimCircle1 = findViewById(R.id.ivCircle1);
        mAnimCircle2 = findViewById(R.id.ivCircle2);
        mAnimCircle3 = findViewById(R.id.ivCircle3);
        mAnimCircle4 = findViewById(R.id.ivCircle4);

        startAnimation(mAnimCircle1, 360f, 2000, 0);
        startAnimation(mAnimCircle2, -360f, 1800, 200);
        startAnimation(mAnimCircle3, 360f, 1800, 200);
        startAnimation(mAnimCircle4, -360f, 2000, 0);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //llFinishOK.setVisibility(View.VISIBLE);
                //startAnimationOK(ivFinishOK, tvFinishOK);
                //isNotLoadAds = true;
            }

            @Override
            public void onAdClosed() {
                mAdView.setVisibility(View.GONE);
                mNativeScrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(ProcessActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(ProcessActivity.this, nativeAd, nativeAdLayout);
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

    private void startAnimation(final ImageView mAnimCircle, float endDegree, int animDuration, long startDelay) {
        oa = ObjectAnimator.ofFloat(mAnimCircle, "rotation", 0f, endDegree);
        oa.setRepeatMode(ValueAnimator.RESTART);
        oa.setRepeatCount(8);
        oa.setStartDelay(startDelay);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.setDuration(animDuration).start();
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimCircle.equals(mAnimCircle4)) {
                    tvFinishText.setVisibility(View.VISIBLE);
                    mClProcess.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startAnimationOK(final ImageView mAnimOK, final TextView mAnimTV_OK, final LinearLayout mAnimLLGo) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimTV_OK, "alpha", 0f, 1f);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(mAnimOK, "alpha", 0f, 1f);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAnimOK, "translationX", 100f, 0f);
        ObjectAnimator oa4 = ObjectAnimator.ofFloat(mAnimLLGo, "alpha", 0f, 1f);
        ObjectAnimator oa5 = ObjectAnimator.ofFloat(mAnimLLGo, "translationY", 100f, 0f);
        oa2.setStartDelay(1000);
        oa3.setStartDelay(1000);
        oa1.setStartDelay(700);
        oa4.setStartDelay(1300);
        oa5.setStartDelay(1300);
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa5.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(300).start();
        oa2.setDuration(300).start();
        oa3.setDuration(300).start();
        oa4.setDuration(300).start();
        oa5.setDuration(300).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //startAnimationDisappearance(mAnimLogo, llApp);
            }
        });
    }

    private void showProgress() {
        speed = 180;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (progress = 0; progress < 100; progress++) {
                    try {
                        speed--;
                        Thread.sleep(speed);
                        pbHorizontal.setProgress(progress);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        oa.end();
                        pbHorizontal.setVisibility(View.GONE);
                        tvProcess.setVisibility(View.GONE);
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                            //llFinishOK.setVisibility(View.VISIBLE);
                            //llGo.setVisibility(View.VISIBLE);
                            //startAnimationOK(ivFinishOK, tvFinishOK, llGo);
                        }
                    }
                });
                //}
            }
        }).
                start();
    }

    public void onClickRateThisApp(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri
                    .parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
            if (!isActivityStarted(intent)) {
                Toast.makeText(
                        this,
                        "Could not open Android market, please check if the market app installed or not. Try again later",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void showAlertDialog() {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ProcessActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        Button btn_positive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = dialogView.findViewById(R.id.dialog_negative_btn);

        // Create the alert dialog
        dialog = builder.create();

        try {
            PreferencesHelper.savePreference("countStarts1", count + 1);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
                onClickRateThisApp(v);
                Log.d("TAG", "Positive btn");
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
                showAlertDialogField();
                Log.d("TAG", "Positive btn");
            }
        });

        // Display the custom alert dialog on interface

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.show();
            }
        }, 2000);  // 1500 milliseconds
    }

    private void showAlertDialogField() {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ProcessActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_bad_rate, null);

        mEtEmail = dialogView.findViewById(R.id.etEmail);
        mEtName = dialogView.findViewById(R.id.etName);
        mEtComment = dialogView.findViewById(R.id.etComment);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        Button btn_positive = dialogView.findViewById(R.id.dialog_pos_btn);

        // Create the alert dialog
        dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog\
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

                mSendmail.sendMail(ProcessActivity.this, sb);
                Log.d("TAG", "Positive btn - send mail!");
                dialog.cancel();
                Log.d("TAG", "Positive btn");
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickGO(View view) {
        startActivity(new Intent(ProcessActivity.this, ProcessActivityNew.class).putExtras(b));
    }

//    @Override
//    protected void onDestroy() {
//
////        if ( mMoPubView != null ){
////            mMoPubView.destroy();
////        }
//
//        super.onDestroy();
//    }
}

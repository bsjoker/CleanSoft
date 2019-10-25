package ru.alphanix.cleansoft.process;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivity;

public class ProcessActivity extends BaseActivity {
    public static final String TAG = "ProcessActivity";

    @Inject
    ProcessActivityPresenter presenter;

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

    @BindView(R.id.ivCircle1)
    ImageView mAnimCircle1;

    @BindView(R.id.ivCircle2)
    ImageView mAnimCircle2;

    @BindView(R.id.ivCircle3)
    ImageView mAnimCircle3;

    @BindView(R.id.ivCircle4)
    ImageView mAnimCircle4;

    @BindView(R.id.fl_adplaceholder)
    FrameLayout mNativeFrameLayout;

    @BindView(R.id.clProcess)
    ConstraintLayout mClProcess;

    @BindView(R.id.scrollView)
    ScrollView mNativeScrollView;

    @BindView(R.id.adViewRect)
    AdView mAdView;

    ObjectAnimator oa;

    private InterstitialAd mInterstitialAd;

    private EditText mEtComment, mEtName, mEtEmail;
    private int progress, speed;
    private AlertDialog dialog;

    private Unbinder mUnbinder;

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;

    private UnifiedNativeAd unifiedNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        mUnbinder = ButterKnife.bind(this);

        ProcessActivityComponent processActivityComponent = (ProcessActivityComponent) App.get(this)
                .getComponentsHolder().getActivityComponent(getClass(), new ProcessActivityModule(this));
        processActivityComponent.inject(this);

        MobileAds.initialize(this, getResources().getString(R.string.appID));

//        SdkConfiguration sdkConfiguration =
//                new SdkConfiguration.Builder("YOUR_MOPUB_AD_UNIT_ID").build();
//
//        MoPub.initializeSdk(getApplicationContext(), sdkConfiguration, null);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intersentialID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //nativeAd = new NativeAd(this, "486963001835819_506222603243192");
        nativeAd = new NativeAd(this, "VID_HD_16_9_46S_LINK#486963001835819_506222603243192");
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {}

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
            public void onAdClicked(Ad ad) {}

            @Override
            public void onLoggingImpression(Ad ad) {}
        });
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clickBackMenu();
                //startActivity(new Intent(ProcessActivity.this, MainMenuActivity.class).putExtra("temp", PreferencesHelper.getSharedPreferences().getInt("curTemp", 40) - r.nextInt(8)));
                startActivity(new Intent(ProcessActivity.this, MainMenuActivity.class));
                finish();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                mNativeFrameLayout.setVisibility(View.GONE);
                mNativeScrollView.setVisibility(View.VISIBLE);
                setConstraintSetForFinishText(R.id.scrollView);
            }
        });
    }

    private void setConstraintSetForFinishText(Integer view) {
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = findViewById(R.id.lp);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.finishText,ConstraintSet.TOP,R.id.lp,ConstraintSet.TOP,8);
        constraintSet.connect(R.id.finishText,ConstraintSet.BOTTOM,view,ConstraintSet.TOP,8);
        constraintSet.applyTo(constraintLayout);
    }

    public void setTitleAndFinishText(String mTitle, String mFinishText) {
        tvTitle.setText(mTitle);
        tvFinishText.setText(mFinishText);
    }

    public void showAdMobAds(){
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    public void refreshAd() {
        mNativeFrameLayout.setVisibility(View.VISIBLE);
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.nativeADMOB_AD_UNIT_ID_finish));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAdLocal) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (unifiedNativeAd != null) {
                    unifiedNativeAd.destroy();
                }
                unifiedNativeAd = unifiedNativeAdLocal;
                FrameLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAdLocal, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(ProcessActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            Log.d(TAG, "INVISIBLE text: " + nativeAd.getAdvertiser());
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
            Log.d(TAG, "Visible text: " + nativeAd.getAdvertiser());
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }

    public void startAnimation(final ImageView mAnimCircle, float endDegree, int animDuration, long startDelay) {
        oa = ObjectAnimator.ofFloat(mAnimCircle, "rotation", 0f, endDegree);
        oa.setRepeatMode(ValueAnimator.RESTART);
        oa.setRepeatCount(8);
        oa.setStartDelay(startDelay);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.setDuration(animDuration).start();
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimCircle.equals(mAnimCircle4)) {
                    tvFinishText.setVisibility(View.VISIBLE);
                    mClProcess.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
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

    public void showProgress() {
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

    public void showAlertDialog() {
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
                presenter.sendMail(mEtName, mEtEmail, mEtComment);
                dialog.cancel();
                Log.d("TAG", "Positive btn");
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (unifiedNativeAd != null) {
            unifiedNativeAd.destroy();
        }
        super.onDestroy();
        mUnbinder.unbind();
        App.get(this).getComponentsHolder().releaseActivityComponent(getClass());
    }

    @Inject
    void setActivity(){
        presenter.setActivity(this);
    }
}

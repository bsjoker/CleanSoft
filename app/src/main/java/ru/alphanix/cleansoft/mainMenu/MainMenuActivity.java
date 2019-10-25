package ru.alphanix.cleansoft.mainMenu;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.google.ads.consent.*;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.Utils.LocaleHelper;
import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.boosting.BoostActivity;
import ru.alphanix.cleansoft.cleaning.CleanCacheFakeActivity;
import ru.alphanix.cleansoft.MenuActivity;
import ru.alphanix.cleansoft.R;
import ru.alphanix.cleansoft.cooling.TempActivity;

public class MainMenuActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    MainMenuActivityPresenter presenter;

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

    @BindView(R.id.fl_adplaceholder)
    FrameLayout mNativeFrameLayout;

    @BindView(R.id.adView)
    AdView mAdView;

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;

    private ConsentForm form;
    private Unbinder mUnbinder;
    private int count = 0;

    private AnimatorSet set;
    private UnifiedNativeAd unifiedNativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        mUnbinder = ButterKnife.bind(this);

        MainMenuActivityComponent mainMenuActivityComponent = (MainMenuActivityComponent)
                App.get(this).getComponentsHolder().getActivityComponent(getClass(), new MainMenuActivityModule());
        mainMenuActivityComponent.inject(this);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        presenter.checkReadyForAds();

        mclCool.setOnClickListener(this);
        mclClear.setOnClickListener(this);
        mclBoost.setOnClickListener(this);
        mclCoolGraph.setOnClickListener(this);
        mclClearGraph.setOnClickListener(this);
        mclBoostGraph.setOnClickListener(this);

        set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_blink);
        presenter.checkTimeDelayAfterClear();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        final ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        String[] publisherIds = {"pub-2215717436233572"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                boolean inEEA = ConsentInformation.getInstance(getApplicationContext())
                        .isRequestLocationInEeaOrUnknown();

                if (inEEA) {
                    Toast.makeText(MainMenuActivity.this,
                            consentStatus.toString(), Toast.LENGTH_LONG).show();
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

        //MobileAds.initialize(this, getResources().getString(R.string.appID));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(getResources().getString(R.string.app_name));
    }

    public void switchOffBoost() {
        mclBoost.setAlpha(0.5f);
        ivCheckBoost.setAlpha(0.0f);
        startAnimationCheck(ivCheckBoost, 0);
        ivStrokeBoost.setVisibility(View.GONE);
        pbVerticalRAMgreen.setAlpha(0.0f);
        pbVerticalRAMred.setVisibility(View.GONE);
        tvPercentRAM.setVisibility(View.GONE);
        if (mclBoost.hasOnClickListeners()) {
            mclBoost.setOnClickListener(null);
            mclBoostGraph.setOnClickListener(null);
        }
        count++;
    }

    public void switchOffClear() {
        mclClear.setAlpha(0.5f);
        ivCheckClear.setAlpha(0.0f);
        startAnimationCheck(ivCheckClear, 0);
        ivStrokeClear.setVisibility(View.GONE);
        pbVerticalROMgreen.setAlpha(0.0f);
        pbVerticalROMred.setVisibility(View.GONE);
        tvPercentROM.setVisibility(View.GONE);
        if (mclClear.hasOnClickListeners()) {
            mclClear.setOnClickListener(null);
            mclClearGraph.setOnClickListener(null);
        }
        count++;
    }

    public void switchOffCool() {
        mclCool.setAlpha(0.5f);
        ivCheckCool.setAlpha(0.0f);
        startAnimationCheck(ivCheckCool, 0);
        ivStrokeCool.setVisibility(View.GONE);
        pbVerticalCPUgreen.setVisibility(View.GONE);
        pbVerticalCPUred.setVisibility(View.GONE);
        tvPercentCPU.setVisibility(View.GONE);
        if (mclCool.hasOnClickListeners()) {
            mclCool.setOnClickListener(null);
            mclCoolGraph.setOnClickListener(null);
        }
    }

    public void setBlink(int num) {
        switch (num) {
            case 1:
                set.setTarget(ivStrokeBoost);
                break;
            case 2:
                set.setTarget(ivStrokeClear);
                break;
            case 3:
                set.setTarget(ivStrokeCool);
                break;
        }
    }

    private void startAnimationCheck(final ImageView mAnimWave, long timeDelay) {
        switch (count) {
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
                if (ivStrokeBoost.getVisibility() == View.GONE
                        && ivStrokeClear.getVisibility() == View.GONE
                        && ivStrokeCool.getVisibility() == View.GONE) {
//                    mNativeScrollView.setVisibility(View.VISIBLE);
//                    inflateAd(nativeAd);
                    mNativeFrameLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()) {
            case R.id.pb_verticalCPUred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        presenter.endAnimation(maxValue, tvPercentCPU);
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
                        presenter.endAnimation(maxValue, tvPercentROM);
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
                        presenter.endAnimation(maxValue, tvPercentRAM);
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

    void setTextOnGraph(TextView tv, String text, int padding_in_px) {
        try {
            tv.setText(text);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            tv.setPadding(0, 0, 0, padding_in_px);
            tv.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


//Блок работы с рекламой----------------------------------------------------------------------------


    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
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

    public void refreshAd() {
        Log.d(TAG, "RefreshAd!");
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources()
                .getString(R.string.nativeADMOB_AD_UNIT_ID));

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
                Toast.makeText(MainMenuActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

//    Прорисовка рекламы Facebook Native Ads
//    private void inflateAd(NativeAd nativeAd) {
//
//        nativeAd.unregisterView();
//
//        // Add the Ad view into the ad container.
//        nativeAdLayout = findViewById(R.id.native_ad_container_main);
//        LayoutInflater inflater = LayoutInflater.from(MainMenuActivity.this);
//        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
//        nativeAdLayout.addView(adView);
//
//        // Add the AdOptionsView
//        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(MainMenuActivity.this, nativeAd, nativeAdLayout);
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

    //Конец блока работы с рекламой-----------------------------------------------------------------

    @Override
    protected void onDestroy() {
        if (unifiedNativeAd != null) {
            unifiedNativeAd.destroy();
        }
        super.onDestroy();
        mUnbinder.unbind();

        if (isFinishing()) {
            App.get(this).getComponentsHolder().releaseActivityComponent(getClass());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getCpuTemp();
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
            case R.id.clCoolGraph:
                startActivity(new Intent(MainMenuActivity.this, TempActivity.class));
                break;
            case R.id.clClear:
            case R.id.clClearGraph:
                startActivity(new Intent(MainMenuActivity.this, CleanCacheFakeActivity.class));
                break;
            case R.id.clBoost:
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


    @Inject
    void setActivity() {
        presenter.setActivity(this);
    }
}

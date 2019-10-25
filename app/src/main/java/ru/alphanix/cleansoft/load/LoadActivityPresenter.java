package ru.alphanix.cleansoft.load;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
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

import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.R;

public class LoadActivityPresenter {
    private final static String TAG = "LoadActivityPresenter";

    private LoadActivity activity;
    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    Locale defLocale;
    Context context;

    public LoadActivityPresenter(Context context) {
        this.context = context;
        checkStartCount();
    }

    private void checkStartCount() {
        int count = PreferencesHelper.getSharedPreferences().getInt("countStarts1", 0);

        if (count < 4) {
            try {
                PreferencesHelper.savePreference("countStarts1", count + 1);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }
    }

    void changeLocale(Context context) {
            defLocale = context.getResources().getConfiguration().locale;
            Locale locale = new Locale(PreferencesHelper.getSharedPreferences().getString("locale", defLocale.getCountry()));
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.setLocale(locale);
            context.getResources().updateConfiguration(configuration,
                    context.getResources().getDisplayMetrics());
            activity.updateTextWithNewLocale();
    }

    void initAds(){
        MobileAds.initialize(activity, context.getResources().getString(R.string.appID));
        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.intersentialIDfirst));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    public void startAnimationWord(final TextView mAnimWave, long timeDelay) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mAnimWave, "translationY", 1000f, 0f);
        oa1.setStartDelay(timeDelay);
        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.setDuration(2000).start();
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mAnimWave.getId() == R.id.tvBoost) {
                    activity.tvBoost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (activity.tvClean.isShown()) {
                    activity.tvOptimize.setVisibility(View.VISIBLE);
                } else {
                    activity.tvClean.setVisibility(View.VISIBLE);
                }

                if (mAnimWave.equals(activity.tvOptimize)) {
                    if (mInterstitialAd.isLoaded()) {
                        activity.showNextActivity();
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                Toast.makeText(activity, "Ad Loaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                Toast.makeText(activity, "Ad Failed To Load", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdClosed() {

                            }
                        });
                    } else {
                        activity.showNextActivity();
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }
            }
        });
    }

    public void startAnimationWave(ImageView mAnimWave, float endPos, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(mAnimWave, "translationX", 0f, endPos);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.setDuration(duration).start();
    }

    public void setActivity(LoadActivity activity){
        this.activity = activity;
    }


}

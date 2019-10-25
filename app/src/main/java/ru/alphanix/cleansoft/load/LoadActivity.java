package ru.alphanix.cleansoft.load;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.Utils.PreferencesHelper;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivity;
import ru.alphanix.cleansoft.PolicyFirstActivity;
import ru.alphanix.cleansoft.R;

public class LoadActivity extends AppCompatActivity {

    @Inject
    LoadActivityPresenter presenter;

    @BindView(R.id.tvBoost)
    TextView tvBoost;

    @BindView(R.id.tvClean)
    TextView tvClean;

    @BindView(R.id.tvOptimize)
    TextView tvOptimize;

    @BindView(R.id.tvPrivacy)
    TextView tvPrivacy;

    @BindView(R.id.ivWave)
    ImageView wave;

    @BindView(R.id.ivWave2)
    ImageView wave2;

    @BindView(R.id.ivWave2double)
    ImageView wave2d;

    @BindView(R.id.ivWave3)
    ImageView wave3;

    @BindView(R.id.ivWave4)
    ImageView wave4;

    @BindView(R.id.ivWave4double)
    ImageView wave4d;

    private Unbinder mUnbinder;
    Context context;
    private boolean isStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        mUnbinder = ButterKnife.bind(this);

        context = getBaseContext();

        LoadActivityComponent loadActivityComponent = (LoadActivityComponent) App.get(this).getComponentsHolder().getActivityComponent(getClass(), new LoadActivityModule(context));
        loadActivityComponent.inject(this);

        presenter.changeLocale(context);
        presenter.initAds();

        setTypeface();
        startAnimationWaves();
        startAnimationWords();
    }

    private void setTypeface() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "roboto.ttf");
        tvBoost.setTypeface(tf);
        tvClean.setTypeface(tf);
        tvOptimize.setTypeface(tf);
        tvPrivacy.setTypeface(tf);
    }

    private void startAnimationWaves() {
        presenter.startAnimationWave(wave, 2000f, 4500);
        presenter.startAnimationWave(wave2, 5500f, 4500);
        presenter.startAnimationWave(wave2d, 5500f, 4500);
        presenter.startAnimationWave(wave3, -2000f, 4500);
        presenter.startAnimationWave(wave4, -5500f, 4500);
        presenter.startAnimationWave(wave4d, -5500f, 4500);
    }

    private void startAnimationWords() {
        presenter.startAnimationWord(tvBoost, 500);
        presenter.startAnimationWord(tvClean, 2500);
        presenter.startAnimationWord(tvOptimize, 4500);
    }

    public void showNextActivity() {
        if (!isStart) {
            startActivity(new Intent(LoadActivity.this, MainMenuActivity.class));
            finish();
        }
    }

    public void onClickPolicy(View view) {
        startActivity(new Intent(LoadActivity.this, PolicyFirstActivity.class));
        isStart = true;
        finish();
    }

    public void updateTextWithNewLocale() {
        setTitle(R.string.app_name);
        tvBoost.setText(R.string.boost);
        tvClean.setText(R.string.clean);
        tvOptimize.setText(R.string.optimize);
        tvPrivacy.setText(R.string.policity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.changeLocale(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        if(isFinishing()){
            App.get(this).getComponentsHolder().releaseActivityComponent(getClass());
        }
    }

    @Inject
    void setActivity() {
        presenter.setActivity(this);
    }
}

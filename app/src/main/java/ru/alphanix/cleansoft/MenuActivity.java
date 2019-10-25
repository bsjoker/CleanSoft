package ru.alphanix.cleansoft;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivity;
import ru.alphanix.cleansoft.setting.SettingActivity;

public class MenuActivity extends BaseActivity implements View.OnClickListener{
    ImageView wave, wave2, wave2d, wave3, wave4, wave4d;
    LinearLayout mLlAppInfo, mLlSetting, mLlPilicy;
    TextView tvTitleMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvTitleMenu = findViewById(R.id.tvTitle);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.includeMenu);

        tvTitleMenu.setText(getResources().getString(R.string.app_name));
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        wave = findViewById(R.id.ivWave);
        wave2 = findViewById(R.id.ivWave2);
        wave2d = findViewById(R.id.ivWave2double);
        wave3 = findViewById(R.id.ivWave3);
        wave4 = findViewById(R.id.ivWave4);
        wave4d = findViewById(R.id.ivWave4double);

        mLlAppInfo = findViewById(R.id.llAppInfo);
        mLlSetting = findViewById(R.id.llSetting);
        mLlPilicy = findViewById(R.id.llPolicy);

        mLlAppInfo.setOnClickListener(this);
        mLlSetting.setOnClickListener(this);
        mLlPilicy.setOnClickListener(this);

        startAnimationWave(wave, 2000f, 3000);
        startAnimationWave(wave2, 5500f, 3000);
        startAnimationWave(wave2d, 5500f, 3000);
        startAnimationWave(wave3, -2000f, 3000);
        startAnimationWave(wave4, -5500f, 3000);
        startAnimationWave(wave4d, -5500f, 3000);
    }

    private void startAnimationWave(ImageView mAnimWave, float endPos, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(mAnimWave, "translationX", 0f, endPos);
        oa.setInterpolator(new AccelerateDecelerateInterpolator());
        oa.setDuration(duration).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MenuActivity.this, MainMenuActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llAppInfo:
                startActivity(new Intent(MenuActivity.this, AppInfoActivity.class));
                break;
            case R.id.llSetting:
                finish();
                startActivity(new Intent(MenuActivity.this, SettingActivity.class));
                break;
            case R.id.llPolicy:
                startActivity(new Intent(MenuActivity.this, PolicyActivity.class));
                break;
        }
    }
}

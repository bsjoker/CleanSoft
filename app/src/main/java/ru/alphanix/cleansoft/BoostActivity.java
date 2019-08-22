package ru.alphanix.cleansoft;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BoostActivity extends AppCompatActivity{
    private final static String TAG = "BoostActivity";
    @BindView(R.id.pb_horizontalCPUred)
    ProgressBar pbHorizontalRAMred;

    @BindView(R.id.pb_horizontalCPUgreen)
    ProgressBar pbHorizontalRAMgreen;

    @BindView(R.id.percentCPUboost)
    TextView tvPercentRAM;

    @BindView(R.id.spaceCPU)
    TextView tvSpaceRAM;

    @BindView(R.id.boostCPU)
    TextView tvBoostRAM;

    @BindView(R.id.llCPUboost)
    LinearLayout llRAM;

    @BindView(R.id.listViewBoost)
    ListView lvApp;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    private Unbinder mUnbinder;
    private List<ApplicationInfo> packagesRun;
    ArrayList<String> packageNames = new ArrayList<>();
    ArrayList<String> packageNamesForKills = new ArrayList<>();
    ArrayList<String> appNames = new ArrayList<>();
    ActivityManager am;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost);

        mUnbinder = ButterKnife.bind(this);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        tvTitle.setText(getResources().getString(R.string.ram));
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Integer percentRAM = (int) calculateRAMMemory();

        if (percentRAM>50) {
            showProgress(pbHorizontalRAMred, 1500, 2000, percentRAM);
            showProgress(pbHorizontalRAMgreen, 0, 3500, percentRAM - (percentRAM-50));
        } else {
            showProgress(pbHorizontalRAMred, 0, 3500, percentRAM);
            showProgress(pbHorizontalRAMgreen, 0, 3500, percentRAM);
        }

        am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        fillListApps(am);

        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                packageNamesForKills.clear();
                for (int i = 0; i < chosen.size(); i++) {
                    // если пользователь выбрал пункт списка,
                    // то выводим его в TextView.
                    if (chosen.valueAt(i)) {
                        packageNamesForKills.add(packageNames.get(chosen.keyAt(i)));
                    }
                }
            }
        });
    }

    private void fillListApps(ActivityManager am) {

        PackageManager pm = getApplicationContext().getPackageManager();
        packagesRun = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packagesRun) {
            boolean system = (packageInfo.flags & packageInfo.FLAG_SYSTEM) > 0;
            boolean stoped = (packageInfo.flags & packageInfo.FLAG_STOPPED) > 0;
            //system apps! get out
            if (!stoped && !system) {
                packageNames.add(packageInfo.packageName.toString());
                appNames.add(packageInfo.loadLabel(pm).toString());
                //am.killBackgroundProcesses(packageInfo.packageName);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, appNames);
        lvApp.setAdapter(adapter);
        for ( int i=0; i < appNames.size(); i++) {
            packageNamesForKills.add(packageNames.get(i));
            lvApp.setItemChecked(i, true);
        }
    }

    private void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()){
            case R.id.pb_horizontalCPUred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tvPercentRAM.setText(String.valueOf(maxValue) + "%");
                        int padding_in_dp = (int) (maxValue*2.5) - 60;
                        final float scale = getResources().getDisplayMetrics().density;
                        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                        llRAM.setPadding(padding_in_px,0,0, 0);
                        tvPercentRAM.setVisibility(View.VISIBLE);
                        tvBoostRAM.setVisibility(View.VISIBLE);
                        tvSpaceRAM.setVisibility(View.VISIBLE);
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
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    private long calculateRAMMemory() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        tvSpaceRAM.setText(Formatter.formatFileSize(getApplicationContext(), mi.totalMem - mi.availMem) + " / " + Formatter.formatFileSize(getApplicationContext(), mi.totalMem));
        Long percentRAMVal = Math.round(((mi.totalMem - mi.availMem) / 10737418.24) / (mi.totalMem / 1073741824.0));
        return percentRAMVal;
    }

    public void onClickBoost(View view) {
        String[] stringApps = packageNamesForKills.toArray(new String[0]);
        for (String packageName : packageNamesForKills){
            am.killBackgroundProcesses(packageName);
            Log.i(TAG, packageName + " killed!");
        }

        Bundle b = new Bundle();
        b.putStringArray("appsKey", stringApps);
        startActivity(new Intent(BoostActivity.this, ProcessActivity.class).putExtra("process", "boost").putExtra("packagesForKills", packageNamesForKills.size()).putExtras(b));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.action_menu){
            startActivity(new Intent(BoostActivity.this, MenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

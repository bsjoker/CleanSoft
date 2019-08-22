package ru.alphanix.cleansoft;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.model.AppsListItem;

public class TempActivity extends AppCompatActivity{
    private final static String TAG = "BoostActivity";

    @BindView(R.id.pb_horizontalCPUred)
    ProgressBar pbHorizontalRAMred;

    @BindView(R.id.pb_horizontalCPUgreen)
    ProgressBar pbHorizontalRAMgreen;

    @BindView(R.id.percentCPUcool)
    TextView tvCPUcool;

    @BindView(R.id.coolCPU)
    TextView tvCoolSystemCPU;

    @BindView(R.id.llCPUcool)
    LinearLayout llCoolCPU;

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
    private int t=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        mUnbinder = ButterKnife.bind(this);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        tvTitle.setText(getResources().getString(R.string.cpu));
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            t = Math.round(getCpuTemp());
        } else {
            t = PreferencesHelper.getSharedPreferences().getInt("curTemp", 40);
        }

        while (t>100){
            t = t / 10;
        }

        //showProgress(pbHorizontalRAMred, 1500, 2000, 85);
        if (t>60) {
            showProgress(pbHorizontalRAMred, 1500, 2000, t);
            showProgress(pbHorizontalRAMgreen, 0, 3500, t-20);
        } else {
            showProgress(pbHorizontalRAMred, 0, 3500, t);
            showProgress(pbHorizontalRAMgreen, 0, 3500, t);
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

    private long addPackage(List<AppsListItem> apps, PackageStats pStats, boolean succeeded) {
        long cacheSize = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            cacheSize += pStats.cacheSize;
        }

        cacheSize += pStats.externalCacheSize;

        if (!succeeded || cacheSize <= 0) {
            return 0;
        }

        return cacheSize;
    }

    private void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()){
            case R.id.pb_horizontalCPUgreen:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (PreferencesHelper.getSharedPreferences().getBoolean("isFahrenheit", false)){
                            double tF = t*1.8 + 32;
                            DecimalFormat df = new DecimalFormat("####0.00");
                            tvCPUcool.setText(df.format(tF) + getResources().getString(R.string.degreeceF));
                        } else {
                            tvCPUcool.setText(t + getResources().getString(R.string.degreece));
                        }
                        int padding_in_dp = (int) (maxValue*2.5) - 50;
                        final float scale = getResources().getDisplayMetrics().density;
                        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                        llCoolCPU.setPadding(padding_in_px,0,0, 0);
                        tvCPUcool.setVisibility(View.VISIBLE);
                        tvCoolSystemCPU.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.action_menu){
            startActivity(new Intent(TempActivity.this, MenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickCool(View view) {
        String[] stringApps = packageNamesForKills.toArray(new String[0]);
        for (String packageName : packageNamesForKills){
            am.killBackgroundProcesses(packageName);
            Log.d(TAG, packageName + " killed!");
        }

        Bundle b = new Bundle();
        b.putStringArray("appsKey", stringApps);
        startActivity(new Intent(TempActivity.this, ProcessActivity.class).putExtra("process", "cool").putExtra("packagesForKills", packageNamesForKills.size()).putExtras(b));
    }
}

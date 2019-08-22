package ru.alphanix.cleansoft;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.model.AppsListItem;


public class CleanCacheFakeActivity extends AppCompatActivity {
    private final static String TAG = "CleanCacheFakeActivity";
    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 123;
    @BindView(R.id.pb_horizontalROMred)
    ProgressBar pbHorizontalROMred;

    @BindView(R.id.pb_horizontalROMgreen)
    ProgressBar pbHorizontalROMgreen;

    @BindView(R.id.percentROM)
    TextView tvPercentROM;

    @BindView(R.id.spaceROM)
    TextView tvSpaceROM;

    @BindView(R.id.cachefilesROM)
    TextView tvCacheROM;

    @BindView(R.id.llROM)
    LinearLayout llROM;

    @BindView(R.id.listViewCache)
    ListView lvApp;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    private AdView mAdView;

    private Unbinder mUnbinder;
    private List<ApplicationInfo> packagesRun;
    ArrayList<String> packageNames = new ArrayList<>();
    ArrayList<String> packageNamesForKills = new ArrayList<>();
    ArrayList<String> appNames = new ArrayList<>();
    ActivityManager am;

    //private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_fake_activity);
        mUnbinder = ButterKnife.bind(this);

        MobileAds.initialize(this, getResources().getString(R.string.appID));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        tvTitle.setText(getResources().getString(R.string.rom));
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        int permissionStatus = ContextCompat.checkSelfPermission(CleanCacheFakeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            //readContacts();
            Log.i(TAG, "Granted!");
            File appsDir = new File("/data/app");

            String[] files = appsDir.list();

            for (int i = 0 ; i < files.length ; i++ ) {
                Log.d(TAG, "File: "+files[i]);

            }
        } else {
            ActivityCompat.requestPermissions(CleanCacheFakeActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
            Log.i(TAG, "Request!");
        }

        Integer percentStorage = (int) calculateStorageFree();
        //Integer percentROM = 76;

        if (percentStorage>80) {
            showProgress(pbHorizontalROMred, 1500, 2000, percentStorage);
            showProgress(pbHorizontalROMgreen, 0, 3500, percentStorage-20);
        } else {
            showProgress(pbHorizontalROMred, 0, 3500, percentStorage);
            showProgress(pbHorizontalROMgreen, 0, 3500, percentStorage);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    //readContacts();
                    Log.i(TAG, "Granted result!");
                } else {
                    // permission denied
                    Log.i(TAG, "Denied result!");
                }
                return;
        }
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
            case R.id.pb_horizontalROMred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tvPercentROM.setText(String.valueOf(maxValue) + "%");
                        int padding_in_dp = (int) (maxValue*2.5) - 60;
                        final float scale = getResources().getDisplayMetrics().density;
                        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                        llROM.setPadding(padding_in_px,0,0, 0);
                        tvPercentROM.setVisibility(View.VISIBLE);
                        tvSpaceROM.setVisibility(View.VISIBLE);
                        tvCacheROM.setVisibility(View.VISIBLE);
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

    private long calculateStorageFree() {
        File r = new File(Environment.getDataDirectory().getAbsolutePath());
        long   total = r.getTotalSpace();
        long   usage = total - r.getFreeSpace();
        tvSpaceROM.setText(Formatter.formatFileSize(getApplicationContext(), usage) + " / " + Formatter.formatFileSize(getApplicationContext(), total));
        long percentStorageVal = Math.round((usage / 10737418.24) / (total / 1073741824.0));
        return percentStorageVal;
    }

    public void onClickBoost(View view) {
        String[] stringApps = packageNamesForKills.toArray(new String[0]);
        for (String packageName : packageNamesForKills){
            am.killBackgroundProcesses(packageName);
            Log.d(TAG, packageName + " killed!");
        }

        Bundle b = new Bundle();
        b.putStringArray("appsKey", stringApps);
        startActivity(new Intent(CleanCacheFakeActivity.this, ProcessActivity.class).putExtra("process", "cache").putExtra("packagesForKills", packageNamesForKills.size()).putExtras(b));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.action_menu){
            startActivity(new Intent(CleanCacheFakeActivity.this, MenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

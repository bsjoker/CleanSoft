package ru.alphanix.cleansoft.cleaning;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.MenuActivity;
import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.process.ProcessActivity;
import ru.alphanix.cleansoft.R;

public class CleanCacheFakeActivity extends BaseActivity {
    private final static String TAG = "CleanCacheFakeActivity";
    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 123;
    private static final long CACHE_APP = Long.MAX_VALUE;
    private CachePackageDataObserver mClearCacheObserver;

    @Inject
    CleanActivityPresenter presenter;

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
    ArrayList<String> packageNamesForKills = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_fake_activity);
        mUnbinder = ButterKnife.bind(this);

        CleanActivityComponent cleanActivityComponent = (CleanActivityComponent) App.get(this)
                .getComponentsHolder().getActivityComponent(getClass(), new CleanActivityModule(this));
        cleanActivityComponent.inject(this);

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE:
                presenter.onPermissionsResult(grantResults);
                return;
        }
    }

    public void fillListApps(ArrayList<String> appNames, final ArrayList<String> packageNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, appNames);
        lvApp.setAdapter(adapter);
        for (int i = 0; i < appNames.size(); i++) {
            packageNamesForKills.add(packageNames.get(i));
            lvApp.setItemChecked(i, true);
        }
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                packageNamesForKills.clear();
                for (int i = 0; i < chosen.size(); i++) {
                    if (chosen.valueAt(i)) {
                        packageNamesForKills.add(packageNames.get(chosen.keyAt(i)));
                    }
                }
            }
        });
    }

    public void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()) {
            case R.id.pb_horizontalROMred:
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        presenter.endAnimation(maxValue);
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

    public void setTextOnGraph(String value, String freeSpace, int padding_in_px) {
        tvPercentROM.setText(value);
        tvSpaceROM.setText(freeSpace);
        llROM.setPadding(padding_in_px, 0, 0, 0);
        tvPercentROM.setVisibility(View.VISIBLE);
        tvSpaceROM.setVisibility(View.VISIBLE);
        tvCacheROM.setVisibility(View.VISIBLE);
    }

    public void onClickClean(View view) {
        String[] stringApps = packageNamesForKills.toArray(new String[0]);
        presenter.killApps(packageNamesForKills);
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
        if (item.getItemId() == R.id.action_menu) {
            startActivity(new Intent(CleanCacheFakeActivity.this, MenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private class CachePackageDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(String packageName, boolean succeeded) {
        }
    }

    void clearCache() {
        Context applicationContext = App.getInstance().getApplicationContext();
        File externalCacheDir = applicationContext.getExternalCacheDir();
        if (externalCacheDir != null) {
            File file = new File(externalCacheDir.getAbsolutePath().replace(applicationContext.getPackageName(), "com.google.android.youtube").toString());
            if (file.exists() && file.isDirectory()) {
                Log.d(TAG, "111:: " + file.getAbsolutePath());
                deleteFile(file);
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            if (list != null) {
                for (String file2 : list) {
                    if (!deleteFile(new File(file, file2))) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    @Inject
    void setActivity() {
        presenter.setActivity(this);
    }
}

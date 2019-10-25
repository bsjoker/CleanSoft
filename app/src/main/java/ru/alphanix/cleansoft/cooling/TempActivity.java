package ru.alphanix.cleansoft.cooling;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.App.App;
import ru.alphanix.cleansoft.MenuActivity;
import ru.alphanix.cleansoft.Utils.LocaleHelper;
import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.process.ProcessActivity;
import ru.alphanix.cleansoft.R;

public class TempActivity extends BaseActivity {
    private final static String TAG = "BoostActivity";
    @Inject
    TempActivityPresenter presenter;

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
    private Context context;

    ArrayList<String> packageNamesForKills = new ArrayList<>();

    private AdView mAdView;
    private int t=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        mUnbinder = ButterKnife.bind(this);

        context = getApplicationContext();

        TempActivityComponent tempActivityComponent = (TempActivityComponent) App.get(this)
                .getComponentsHolder().getActivityComponent(getClass(), new TempActivityModule(context));
        tempActivityComponent.inject(this);

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

        presenter.getCpuTemp();
    }

    public void fillListApps(ArrayList<String> appNames, final ArrayList<String> packageNames) {
//
//        PackageManager pm = getApplicationContext().getPackageManager();
//        packagesRun = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//
//        for (ApplicationInfo packageInfo : packagesRun) {
//            boolean system = (packageInfo.flags & packageInfo.FLAG_SYSTEM) > 0;
//            boolean stoped = (packageInfo.flags & packageInfo.FLAG_STOPPED) > 0;
//            //system apps! get out
//            if (!stoped && !system) {
//
//                packageNames.add(packageInfo.packageName.toString());
//                appNames.add(packageInfo.loadLabel(pm).toString());
//                //am.killBackgroundProcesses(packageInfo.packageName);
//            }
//        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, appNames);
        lvApp.setAdapter(adapter);
        for ( int i=0; i < appNames.size(); i++) {
            packageNamesForKills.add(packageNames.get(i));
            lvApp.setItemChecked(i, true);
        }

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

    public void showProgress(ProgressBar pb, int delay, int duration, final int maxValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, maxValue);
        switch (pb.getId()){
            case R.id.pb_horizontalCPUgreen:
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

    public void setTextOnGraph(String value, int padding_in_px) {
        tvCPUcool.setText(value);
        tvCPUcool.setVisibility(View.VISIBLE);
        tvCoolSystemCPU.setVisibility(View.VISIBLE);
        llCoolCPU.setPadding(padding_in_px,0,0, 0);
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
        presenter.killApps(packageNamesForKills);

        Bundle b = new Bundle();
        b.putStringArray("appsKey", stringApps);
        startActivity(new Intent(TempActivity.this, ProcessActivity.class).putExtra("process", "cool").putExtra("packagesForKills", packageNamesForKills.size()).putExtras(b));
    }

    @Override
    protected void onResume() {
        super.onResume();
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

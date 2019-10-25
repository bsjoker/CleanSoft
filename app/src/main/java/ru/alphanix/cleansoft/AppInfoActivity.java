package ru.alphanix.cleansoft;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ru.alphanix.cleansoft.base.BaseActivity;

public class AppInfoActivity extends BaseActivity {
    TextView tvTitleMenu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);

        tvTitleMenu = findViewById(R.id.tvTitle);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.include);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        tvTitleMenu.setText(getResources().getString(R.string.appinfo));
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MenuBack", "Back clicked");
                finish();
            }
        });
    }
}

package ru.alphanix.cleansoft;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.alphanix.cleansoft.base.BaseActivity;

public class PolicyActivity extends BaseActivity {
    @BindView(R.id.include)
    Toolbar mActionBarToolbar;

    @BindView(R.id.tvTitle)
    TextView tvTitleMenu;

    @BindView(R.id.textViewPolicy)
    TextView tvPolicy;

    private Unbinder mUnbinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        mUnbinder = ButterKnife.bind(this);

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        tvTitleMenu.setText(getResources().getString(R.string.policity));
        tvTitleMenu.setMovementMethod(new ScrollingMovementMethod());
        tvPolicy.setText(getResources().getString(R.string.policy));
        tvPolicy.setMovementMethod(new ScrollingMovementMethod());

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

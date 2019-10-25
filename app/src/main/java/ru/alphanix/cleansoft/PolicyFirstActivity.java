package ru.alphanix.cleansoft;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import ru.alphanix.cleansoft.base.BaseActivity;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivity;

public class PolicyFirstActivity extends BaseActivity {
    private TextView tvPolicy;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_first);

        tvPolicy = findViewById(R.id.tvPolicy);
        tvPolicy.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClickBack(View view) {
        startActivity(new Intent(PolicyFirstActivity.this, MainMenuActivity.class));
        finish();
    }
}

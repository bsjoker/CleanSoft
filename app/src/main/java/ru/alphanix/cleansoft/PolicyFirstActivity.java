package ru.alphanix.cleansoft;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class PolicyFirstActivity extends AppCompatActivity {
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
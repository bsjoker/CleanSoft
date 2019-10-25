package ru.alphanix.cleansoft.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import ru.alphanix.cleansoft.Utils.LocaleHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}

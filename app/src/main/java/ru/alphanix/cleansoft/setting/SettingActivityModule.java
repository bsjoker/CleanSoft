package ru.alphanix.cleansoft.setting;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class SettingActivityModule implements ActivityModule {
    private final Context context;

    public SettingActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    SettingActivityPresenter provideSettingActivityPresenter(){
        return new SettingActivityPresenter(context);
    }
}

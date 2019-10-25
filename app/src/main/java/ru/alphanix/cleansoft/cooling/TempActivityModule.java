package ru.alphanix.cleansoft.cooling;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class TempActivityModule implements ActivityModule {
    private final Context context;

    public TempActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    TempActivityPresenter provideTempActivityPresenter(){
        return new TempActivityPresenter(context);
    }
}

package ru.alphanix.cleansoft.load;

import android.content.Context;
import android.os.Bundle;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class LoadActivityModule implements ActivityModule {

    private final Context context;

    public LoadActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    LoadActivityPresenter provideLoadActivityPresenter(){
        return new LoadActivityPresenter(context);
    }
}

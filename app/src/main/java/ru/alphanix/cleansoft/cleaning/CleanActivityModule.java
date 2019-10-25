package ru.alphanix.cleansoft.cleaning;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class CleanActivityModule implements ActivityModule {
    private final Context context;

    public CleanActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    CleanActivityPresenter provideCleanActivityPresenter(){
        return new CleanActivityPresenter(context);
    }
}

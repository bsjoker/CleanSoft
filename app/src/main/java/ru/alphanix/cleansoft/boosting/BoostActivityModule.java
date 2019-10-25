package ru.alphanix.cleansoft.boosting;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;
import ru.alphanix.cleansoft.cooling.TempActivityPresenter;

@Module
public class BoostActivityModule implements ActivityModule {
    private final Context context;

    public BoostActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    BoostActivityPresenter provideBoostActivityPresenter(){
        return new BoostActivityPresenter(context);
    }
}

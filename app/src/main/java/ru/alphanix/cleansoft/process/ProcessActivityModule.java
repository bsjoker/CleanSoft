package ru.alphanix.cleansoft.process;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class ProcessActivityModule implements ActivityModule {
    private final Context context;

    public ProcessActivityModule(Context context) {
        this.context = context;
    }

    @ActivityScope
    @Provides
    ProcessActivityPresenter provideProcessActivityPresenter(){
        return new ProcessActivityPresenter(context);
    }
}

package ru.alphanix.cleansoft.App;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import ru.alphanix.cleansoft.boosting.BoostActivity;
import ru.alphanix.cleansoft.boosting.BoostActivityComponent;
import ru.alphanix.cleansoft.cleaning.CleanActivityComponent;
import ru.alphanix.cleansoft.cleaning.CleanCacheFakeActivity;
import ru.alphanix.cleansoft.cooling.TempActivity;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;
import ru.alphanix.cleansoft.cooling.TempActivityComponent;
import ru.alphanix.cleansoft.load.LoadActivity;
import ru.alphanix.cleansoft.load.LoadActivityComponent;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivity;
import ru.alphanix.cleansoft.mainMenu.MainMenuActivityComponent;
import ru.alphanix.cleansoft.process.ProcessActivity;
import ru.alphanix.cleansoft.process.ProcessActivityComponent;
import ru.alphanix.cleansoft.setting.SettingActivity;
import ru.alphanix.cleansoft.setting.SettingActivityComponent;

@Module (subcomponents = {LoadActivityComponent.class, MainMenuActivityComponent.class,
        TempActivityComponent.class, BoostActivityComponent.class, CleanActivityComponent.class,
        ProcessActivityComponent.class, SettingActivityComponent.class})
public class ApplicationModule {
    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @AppScope
    @Provides
    Context provideContext() {
        return context;
    }

    @Provides
    @IntoMap
    @ClassKey(LoadActivity.class)
    ActivityComponentBuilder provideLoadActivityBuilder(LoadActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(MainMenuActivity.class)
    ActivityComponentBuilder provideMainMenuActivityBuilder(MainMenuActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(TempActivity.class)
    ActivityComponentBuilder provideTempActivityBuilder(TempActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(BoostActivity.class)
    ActivityComponentBuilder provideBoostActivityBuilder(BoostActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(CleanCacheFakeActivity.class)
    ActivityComponentBuilder provideCleanActivityBuilder(CleanActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(ProcessActivity.class)
    ActivityComponentBuilder provideProcessActivityBuilder(ProcessActivityComponent.Builder builder) {
        return builder;
    }

    @Provides
    @IntoMap
    @ClassKey(SettingActivity.class)
    ActivityComponentBuilder provideSettingActivityBuilder(SettingActivityComponent.Builder builder) {
        return builder;
    }
}

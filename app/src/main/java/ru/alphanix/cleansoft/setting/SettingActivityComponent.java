package ru.alphanix.cleansoft.setting;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = SettingActivityModule.class)
public interface SettingActivityComponent extends ActivityComponent<SettingActivity> {
    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<SettingActivityComponent, SettingActivityModule>{

    }
}

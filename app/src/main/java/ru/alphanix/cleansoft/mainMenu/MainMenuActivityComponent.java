package ru.alphanix.cleansoft.mainMenu;

import dagger.Component;
import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;
import ru.alphanix.cleansoft.load.LoadActivityModule;

@ActivityScope
@Subcomponent(modules = {MainMenuActivityModule.class})
public interface MainMenuActivityComponent extends ActivityComponent<MainMenuActivity> {
    @Subcomponent.Builder
        interface Builder extends ActivityComponentBuilder<MainMenuActivityComponent, MainMenuActivityModule>{
    }
}

package ru.alphanix.cleansoft.mainMenu;

import dagger.Module;
import dagger.Provides;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityModule;

@Module
public class MainMenuActivityModule implements ActivityModule {
    @ActivityScope
    @Provides
    MainMenuActivityPresenter provideMainMenuActivityPresenter(){
        return new MainMenuActivityPresenter();
    }
}

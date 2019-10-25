package ru.alphanix.cleansoft.App;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface AppComponent {
    void injectComponentsHolder(ComponentsHolder componentsHolder);
}

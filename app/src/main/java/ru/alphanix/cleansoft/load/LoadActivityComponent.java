package ru.alphanix.cleansoft.load;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = {LoadActivityModule.class})
public interface LoadActivityComponent extends ActivityComponent<LoadActivity> {
    @Subcomponent.Builder
        interface Builder extends ActivityComponentBuilder<LoadActivityComponent, LoadActivityModule>{
    }
}

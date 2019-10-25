package ru.alphanix.cleansoft.cooling;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = TempActivityModule.class)
public interface TempActivityComponent extends ActivityComponent<TempActivity> {
    @Subcomponent.Builder
        interface Builder extends ActivityComponentBuilder<TempActivityComponent, TempActivityModule>{

    }
}

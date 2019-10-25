package ru.alphanix.cleansoft.cleaning;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = CleanActivityModule.class)
public interface CleanActivityComponent extends ActivityComponent<CleanCacheFakeActivity> {
    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<CleanActivityComponent, CleanActivityModule>{

    }
}

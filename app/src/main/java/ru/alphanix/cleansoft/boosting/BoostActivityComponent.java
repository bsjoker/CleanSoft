package ru.alphanix.cleansoft.boosting;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = BoostActivityModule.class)
public interface BoostActivityComponent extends ActivityComponent<BoostActivity> {
    @Subcomponent.Builder
        interface Builder extends ActivityComponentBuilder<BoostActivityComponent, BoostActivityModule>{

    }
}

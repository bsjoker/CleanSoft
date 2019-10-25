package ru.alphanix.cleansoft.process;

import dagger.Subcomponent;
import ru.alphanix.cleansoft.ActivityScope;
import ru.alphanix.cleansoft.base.ActivityComponent;
import ru.alphanix.cleansoft.base.ActivityComponentBuilder;

@ActivityScope
@Subcomponent(modules = ProcessActivityModule.class)
public interface ProcessActivityComponent extends ActivityComponent<ProcessActivity> {
    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<ProcessActivityComponent, ProcessActivityModule>{

    }
}

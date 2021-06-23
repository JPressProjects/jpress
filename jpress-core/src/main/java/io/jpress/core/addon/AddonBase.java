package io.jpress.core.addon;

import io.jpress.core.module.ModuleBase;

public abstract class AddonBase extends ModuleBase implements Addon {


    @Override
    public void onStart() {
        //在 app 启动的时候，主动调用 插件的 onStart 方法
        onStart(null);
    }
}

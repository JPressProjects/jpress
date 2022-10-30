package io.jpress.module.product.service;

import io.jboot.core.listener.JbootAppListenerBase;
import io.jpress.JPressOptions;
import io.jpress.module.product.service.search.ProductSearchEngineIndexRebuildTask;
import io.jpress.module.product.service.task.ProductViewsCountUpdateTask;

public class ProviderModuleIniter extends JbootAppListenerBase {

    @Override
    public void onStart() {
        JPressOptions.addListener(new ProductSearchEngineIndexRebuildTask());
    }

    @Override
    public void onStop() {
        ProductViewsCountUpdateTask.refreshCount();
    }
}

package io.jpress.module.article.service;

import io.jboot.core.listener.JbootAppListenerBase;
import io.jpress.JPressOptions;
import io.jpress.module.article.service.search.ArticleSearchEngineIndexRebuildTask;

public class ProviderModuleIniter extends JbootAppListenerBase {

    @Override
    public void onStart() {
        JPressOptions.addListener(new ArticleSearchEngineIndexRebuildTask());
    }
}

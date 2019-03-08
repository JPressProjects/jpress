package io.jpress.addon.articlemeta.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.service.JbootServiceBase;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;
import io.jpress.addon.articlemeta.service.ArticleMetaInfoService;

import java.util.List;

@Bean
public class ArticleMetaInfoServiceProvider extends JbootServiceBase<ArticleMetaInfo> implements ArticleMetaInfoService {

    @Override
    @Cacheable(name = "articleMeta", key = "all")
    public List<ArticleMetaInfo> findAll() {
        return super.findAll();
    }

    @Override
    @CacheEvict(name = "articleMeta", key = "all")
    public void shouldUpdateCache(int action, Object data) {
        super.shouldUpdateCache(action, data);
    }
}
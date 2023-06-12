package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.model.ArticleMetaInfo;
import io.jpress.module.article.service.ArticleMetaInfoService;

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
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }
}
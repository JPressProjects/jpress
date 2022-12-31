package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.TemplateBlockOption;
import io.jpress.service.TemplateBlockOptionService;

@Bean
public class TemplateBlockOptionServiceProvider extends JPressServiceBase<TemplateBlockOption> implements TemplateBlockOptionService {

    @Override
    @Cacheable(name = "templateBlockOption", nullCacheEnable = true)
    public TemplateBlockOption findById(Object templateId, Long siteId) {
        return DAO.findByIds(templateId, siteId);
    }

    @Override
    @CachesEvict({
            @CacheEvict(name = "templateBlockOption", key = "*")
    })
    public void shouldUpdateCache(int action, Model model, Object id) {

    }
}
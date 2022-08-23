package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.SiteInfo;
import io.jpress.service.SiteInfoService;

import java.util.List;

/**
 * 注意：此类不能继承 JPressServiceBase，否则其 site_id 字段将会被自动设置
 */
@Bean
public class SiteInfoServiceProvider extends JbootServiceBase<SiteInfo> implements SiteInfoService {


    @Override
    @Cacheable(name = "site_info")
    public List<SiteInfo> findAll() {
        return super.findAll();
    }


    @Override
    public void shouldUpdateCache(int action, Model model, Object id) {
        AopCache.removeAll("site_info");
    }
}
package io.jpress.module.page.sitemap;

import com.jfinal.aop.Inject;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.impl.NewestSitemapProvider;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class NewstPageSitemapProvider implements NewestSitemapProvider {

    @Inject
    private SinglePageService pageService;


    @Override
    public List<Sitemap> getSitemaps() {
        List<SinglePage> pageList = pageService.findAll();
        if (pageList == null || pageList.isEmpty()) {
            return null;
        }
        return pageList.stream()
                .filter(page -> page.getModified() != null
                        && DateUtils.addDays(new Date(),-3).getTime() <= page.getModified().getTime())
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }

}

package io.jpress.module.page.sitemap;


import io.jpress.module.page.model.SinglePage;
import io.jpress.web.sitemap.Sitemap;

class Util {

    public static Sitemap toSitemap(SinglePage page) {
        Sitemap sitemap = new Sitemap();
        sitemap.setChangefreq(Sitemap.CHANGEFREQ_WEEKLY);
        sitemap.setLoc(page.getUrl());
        sitemap.setLastmod(page.getModified());
        sitemap.setPriority(0.5f);
        return sitemap;
    }
}

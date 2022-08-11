package io.jpress.module.product.sitemap;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.List;
import java.util.stream.Collectors;

public class ProductSitemapProvider implements SitemapProvider {
    @Inject
    private ProductService productService;



    @Override
    public List<Sitemap> getSitemaps() {

        Page<Product> articlePage = productService.paginateInNormal(1, 100);
        if (articlePage.getList().isEmpty()) {
            return null;
        }

        return articlePage.getList().stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}

package io.jpress.module.product.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.interceptor.AdminInterceptor;
import io.jpress.web.interceptor.TemplateInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

import java.util.List;

@RequestMapping("/admin/product/preview")
@Before({
        TemplateInterceptor.class,
        AdminInterceptor.class,
        UserInterceptor.class,})
public class _PreviewProductController extends TemplateControllerBase {

    @Inject
    private ProductService productService;

    @Inject
    private ProductCategoryService categoryService;

    public void index() {
        Product product = getProduct();

        render404If(product == null );

        product.setTitle("（预览中...）" + product.getTitle());

        //设置页面的seo信息
        setSeoInfos(product);


        //设置菜单高亮
        doFlagMenuActive(product);

        //记录当前浏览量
        productService.doIncProductViewCount(product.getId());

        setAttr("product", product);

        render(product.getHtmlView());
    }

    private void setSeoInfos(Product product) {
        setSeoTitle(product.getTitle());
        setSeoKeywords(product.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(product.getMetaDescription())
                ? CommonsUtils.maxLength(product.getText(), 100)
                : product.getMetaDescription());
    }


    private Product getProduct() {
        String idOrSlug = getIdOrSlug();
        if (StrUtil.isBlank(idOrSlug)){
            return null;
        }
        return StrUtil.isNumeric(idOrSlug)
                ? productService.findById(idOrSlug)
                : productService.findFirstBySlug(StrUtil.urlDecode(idOrSlug));
    }


    private void doFlagMenuActive(Product product) {

        setMenuActive(menu -> menu.isUrlStartWidth(product.getUrl()));

        List<ProductCategory> productCategories = categoryService.findListByProductId(product.getId());
        if (productCategories == null || productCategories.isEmpty()) {
            return;
        }

        setMenuActive(menu -> {
            if ("product_category".equals(menu.getRelativeTable())) {
                for (ProductCategory category : productCategories) {
                    if (category.getId().equals(menu.getRelativeId())) {
                        return true;
                    }
                }
            }
            return false;
        });

    }




}

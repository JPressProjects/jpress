package io.jpress.module.product.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.product.model.Category;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anjie 安杰 QQ489879492
 * @version V1.0
 * @Title: 商品多级分类
 */
@JFinalDirective("productCategoriesList")
public class ProductCategoriesListDirective extends JbootDirectiveBase {

    @Inject
    private ProductCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        List<Category> categoryList= getCategoryList();
        scope.setLocal("productCategoriesList", categoryList);
        renderBody(env, scope, writer);
    }

    /**
     * 获取菜单列表
     */
    private List<Category> getCategoryList(){

        List<Category> categoryList = new ArrayList<>();
        List<ProductCategory> categories = categoryService._findListByType(ProductCategory.TYPE_CATEGORY);
        for(ProductCategory p:categories){
            if(p.isTop()){
                Category category = new Category();
                category.setProductCategory(p);
                List<Category> cList = new ArrayList<>();
                categoryRecursion(cList,categories,p);
                category.setPlist(cList);
                categoryList.add(category);
            }
        }

        return categoryList;
    }

    /**
     * 子类递归方法
     * @param cList
     * @param categories
     * @param p
     */
    private void categoryRecursion(List<Category> cList, List<ProductCategory> categories, ProductCategory p){

        for(ProductCategory p1:categories){
            if(!p1.isTop()&&p1.getParentId()==p.getId()){
                Category c = new Category();
                c.setProductCategory(p1);
                List<Category> cList1 = new ArrayList<>();
                categoryRecursion(cList1,categories,p1);
                c.setPlist(cList1);
                cList.add(c);
            }
        }

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

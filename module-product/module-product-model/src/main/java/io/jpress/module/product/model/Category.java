package io.jpress.module.product.model;

import java.util.List;

/**
 * anjie QQ489879492
 * 商品层级分类
 */
public class Category {
    //分类
    private ProductCategory productCategory;
    //分类的子类
    List<Category> plist;

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public List<Category> getPlist() {
        return plist;
    }

    public void setPlist(List<Category> plist) {
        this.plist = plist;
    }

    @Override
    public String toString() {
        return "Category{" +
                "productCategory=" + productCategory +
                ", plist=" + plist +
                '}';
    }
}

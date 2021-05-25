package io.jpress.module.product;

import io.jpress.commons.url.CommonsFlatUrlProcesser;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductFlatUrlProcesser extends CommonsFlatUrlProcesser {

    private static Map<String,String> prefixAndTargets = new LinkedHashMap<>();
    static {
        prefixAndTargets.put("/product-category-","/product/category/");
        prefixAndTargets.put("/product-tag-","/product/tag/");
        prefixAndTargets.put("/product-","/product/");
    }


    public ProductFlatUrlProcesser() {
        super(prefixAndTargets);
    }

}


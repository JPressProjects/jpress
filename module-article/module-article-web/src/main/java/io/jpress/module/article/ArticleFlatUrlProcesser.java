package io.jpress.module.article;

import io.jpress.commons.url.CommonsFlatUrlProcesser;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArticleFlatUrlProcesser extends CommonsFlatUrlProcesser {

    private static Map<String,String> prefixAndTargets = new LinkedHashMap<>();
    static {
        prefixAndTargets.put("/article-category-","/article/category/");
        prefixAndTargets.put("/article-tag-","/article/tag/");
        prefixAndTargets.put("/article-","/article/");
    }


    public ArticleFlatUrlProcesser() {
        super(prefixAndTargets);
    }

}


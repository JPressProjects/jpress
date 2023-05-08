package io.jpress.module.article;

import io.jpress.commons.url.CommonsFlatUrlProcesser;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArticleFlatUrlProcesser extends CommonsFlatUrlProcesser {

    private static Map<String,String> prefixAndTargets = new LinkedHashMap<>();
    static {
        prefixAndTargets.put("/category-","/category/");
        prefixAndTargets.put("/tag-","/tag/");
        prefixAndTargets.put("/article-","/article/");
        prefixAndTargets.put("/articles-","/articles/");
    }


    public ArticleFlatUrlProcesser() {
        super(prefixAndTargets);
    }

}


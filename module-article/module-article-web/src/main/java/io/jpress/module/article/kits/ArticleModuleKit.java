package io.jpress.module.article.kits;

import io.jboot.Jboot;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConstants;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.service.OptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.kits
 */
public class ArticleModuleKit {

    public static String getCategoryUrl(ArticleCategory category) {
        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return category.getUrl("");
        }

        String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? category.getUrl("") : category.getUrl(suffix);
    }


    public static String getArticleUrl(Article article) {
        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return article.getUrl("");
        }

        String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? article.getUrl("") : article.getUrl(suffix);
    }

}

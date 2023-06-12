package io.jpress.module.article.directive;


import com.jfinal.aop.Aop;
import io.jboot.utils.StrUtil;
import io.jpress.module.article.model.ArticleMetaRecord;
import io.jpress.module.article.service.ArticleMetaRecordService;

public class ArticleMetaFunctions {


    public String articleMeta(Object articleId, String fieldName) {
        return articleMeta(articleId, fieldName, null);
    }


    public String articleMeta(Object articleId, String fieldName, String defaultValue) {
        ArticleMetaRecordService metaRecordService = Aop.get(ArticleMetaRecordService.class);
        ArticleMetaRecord record = metaRecordService.findByArticleIdAndFieldName(articleId, fieldName);
        return record != null && StrUtil.isNotBlank(record.getValue())
                ? record.getValue()
                : defaultValue;
    }

}

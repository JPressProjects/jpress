package io.jpress.addon.articlemeta.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.addon.articlemeta.service.ArticleMetaRecordService;
import io.jpress.addon.articlemeta.model.ArticleMetaRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class ArticleMetaRecordServiceProvider extends JbootServiceBase<ArticleMetaRecord> implements ArticleMetaRecordService {

    @Override
    public ArticleMetaRecord findByArticleIdAndFieldName(Object articleId, String fieldName) {
        Columns columns = Columns.create("article_id", articleId)
                .eq("field_name", fieldName);
        return DAO.findFirstByColumns(columns);
    }
}
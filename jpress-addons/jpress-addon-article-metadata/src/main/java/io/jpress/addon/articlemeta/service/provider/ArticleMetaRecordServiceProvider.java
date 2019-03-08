package io.jpress.addon.articlemeta.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.addon.articlemeta.model.ArticleMetaRecord;
import io.jpress.addon.articlemeta.service.ArticleMetaRecordService;

import java.util.List;

@Bean
public class ArticleMetaRecordServiceProvider extends JbootServiceBase<ArticleMetaRecord> implements ArticleMetaRecordService {

    @Override
    public ArticleMetaRecord findByArticleIdAndFieldName(Object articleId, String fieldName) {
        Columns columns = Columns.create("article_id", articleId)
                .eq("field_name", fieldName);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public void batchSaveOrUpdate(List<ArticleMetaRecord> records) {
        for (ArticleMetaRecord record : records) {
            ArticleMetaRecord dbRecord = findByArticleIdAndFieldName(record.getArticleId(), record.getFieldName());
            if (dbRecord != null) {
                record.setId(dbRecord.getId());
            }
            saveOrUpdate(record);
        }
    }
}
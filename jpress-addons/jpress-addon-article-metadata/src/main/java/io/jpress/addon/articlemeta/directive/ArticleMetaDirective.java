package io.jpress.addon.articlemeta.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.addon.articlemeta.model.ArticleMetaRecord;
import io.jpress.addon.articlemeta.service.ArticleMetaRecordService;
import io.jpress.module.article.model.Article;

import java.io.IOException;

/**
 * article 元数据标签
 * @author eric
 */
public class ArticleMetaDirective extends JbootDirectiveBase {

    @Inject
    private ArticleMetaRecordService metaRecordService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Article article = (Article) scope.get("article");
        Long id = getParaToLong("articleId", scope);
        String fieldName = getPara("fieldName", scope);

        if (article == null && id == null) {
            throw new RuntimeException("article and articleId is null");
        }

        if (id == null) {
            id = article.getId();
        }

        ArticleMetaRecord record = metaRecordService.findByArticleIdAndFieldName(id, fieldName);
        if (record != null && StrUtil.isNotBlank(record.getValue())) {
            try {
                writer.write(record.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

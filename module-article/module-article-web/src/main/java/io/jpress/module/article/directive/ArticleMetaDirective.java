package io.jpress.module.article.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleMetaRecord;
import io.jpress.module.article.service.ArticleMetaRecordService;

@JFinalDirective("articleMeta")
public class ArticleMetaDirective extends JbootDirectiveBase {

    @Inject
    private ArticleMetaRecordService metaRecordService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Article article = (Article) scope.get("article");

        String fieldName = getPara(0, scope);
        Object defaultValue = getPara(1, scope);

        if (StrUtil.isBlank(fieldName)) {
            throw new RuntimeException("#articleMeta() args is null or empty");
        }

        if (article == null) {
            throw new RuntimeException("can not get article in context");
        }

        ArticleMetaRecord record = metaRecordService.findByArticleIdAndFieldName(article.getId(), fieldName);
        if (record != null && StrUtil.isNotBlank(record.getValue())) {
            renderText(writer, record.getValue());
        } else if (defaultValue != null) {
            renderText(writer,String.valueOf(defaultValue));
        }

    }
}

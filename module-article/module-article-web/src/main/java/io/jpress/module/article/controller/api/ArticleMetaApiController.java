package io.jpress.module.article.controller.api;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleMetaRecord;
import io.jpress.module.article.service.ArticleMetaRecordService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.ApiControllerBase;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/articlemeta/api")
public class ArticleMetaApiController extends ApiControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleMetaRecordService metaRecordService;


    public void index() {
        Long articleId = getLong("articleId");
        if (articleId == null) {
            renderFailJson();
            return;
        }


        Article article = articleService.findById(articleId);
        if (article == null) {
            renderFailJson();
            return;
        }

        String fieldNames = getPara("fieldNames");
        if (StrUtil.isBlank(fieldNames)) {
            renderFailJson();
            return;
        }

        Map<String, Object> metas = new HashMap<>();
        if (fieldNames.contains(",")) {
            String[] fields = fieldNames.split(",");
            for (String field : fields) {
                if (StrUtil.isNotBlank(field)) {
                    ArticleMetaRecord meta = metaRecordService.findByArticleIdAndFieldName(articleId, field.trim());
                    if (meta != null) {
                        metas.put(field.trim(), meta.getValue());
                    }
                }
            }
        } else {
            ArticleMetaRecord meta = metaRecordService.findByArticleIdAndFieldName(articleId, fieldNames.trim());
            if (meta != null) {
                metas.put(fieldNames.trim(), meta.getValue());
            }
        }

        renderOkJson("metas", metas);
    }


}

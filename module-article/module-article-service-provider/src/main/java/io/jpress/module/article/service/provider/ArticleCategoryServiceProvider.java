package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Bean
@Singleton
public class ArticleCategoryServiceProvider extends JbootServiceBase<ArticleCategory> implements ArticleCategoryService {


    @Override
    public List<ArticleCategory> findListByType(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "id desc");
    }


    @Override
    public Page<ArticleCategory> paginateByType(int page, int pagesize, String type) {
        return DAO.paginateByColumn(page, pagesize, Column.create("type", type), "id desc");
    }

    @Override
    public List<ArticleCategory> findListByArticleId(long articleId) {
        List<Record> mappings = Db.find("select * from article_category_mapping where article_id = ?", articleId);
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }

        List<ArticleCategory> categoryList = new ArrayList<>();
        for (Record mapping : mappings) {
            ArticleCategory articleCategory = DAO.findById((long) mapping.get("category_id"));
            if (articleCategory != null) {
                categoryList.add(articleCategory);
            }
        }

        return categoryList;
    }

    @Override
    public List<ArticleCategory> findListByArticleId(long articleId, String type) {
        List<ArticleCategory> categoryList = findListByArticleId(articleId);
        return categoryList.stream().filter(category -> type.equals(category.getType())).collect(Collectors.toList());
    }


    @Override
    public List<ArticleCategory> doNewOrFindByTagString(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ArticleCategory> articleCategories = new ArrayList<>();
        for (String tag : tags) {
            Columns columns = Columns.create("type", ArticleCategory.TYPE_TAG);
            columns.add(Column.create("slug", tag));

            ArticleCategory articleCategory = DAO.findFirstByColumns(columns);

            if (articleCategory == null) {
                articleCategory = new ArticleCategory();
                articleCategory.setTitle(tag);
                articleCategory.setSlug(tag);
                articleCategory.setType(ArticleCategory.TYPE_TAG);
                articleCategory.save();
            }

            articleCategories.add(articleCategory);
        }

        return articleCategories;
    }

    @Override
    public Long[] findCategoryIdsByArticleId(long articleId) {
        List<Record> records = Db.find("select * from article_category_mapping where article_id = ?", articleId);
        if (records == null || records.isEmpty())
            return null;

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }


    @Override
    public ArticleCategory findFirstByTypeAndSlug(String type, String slug) {
        return DAO.findFirstByColumns(Columns.create("type", type).eq("slug", slug));
    }
}
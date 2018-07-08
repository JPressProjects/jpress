package io.jpress.module.article.model;

import io.jboot.db.annotation.Table;
import io.jpress.module.article.model.base.BaseArticleCategory;

/**
 * 此类用来定义 文章的类型，包含了：分类、标签和专题
 * 分类和标签只是对文章的逻辑归类
 * 专题可以用于知识付费
 * <p>
 * 标签和专题  只能有一个层级，分类可以有多个层级
 */
@Table(tableName = "article_category", primaryKey = "id")
public class ArticleCategory extends BaseArticleCategory<ArticleCategory> {

    /**
     * 普通的分类
     * 分类可以有多个层级
     */
    public static final String TYPE_CATEGORY = "category";

    /**
     * 标签
     * 标签只有一个层级
     */
    public static final String TYPE_TAG = "tag";

    /**
     * 专题，专题是可以设置付费的
     * 专题只有一个层级
     */
    public static final String TYPE_SUBJECT = "subject";

    /**
     * 用户自建专题，也可以进行收费，目前暂不考虑这部分的实现
     */
    public static final String TYPE_USER_SUBJECT = "user_subject";

}

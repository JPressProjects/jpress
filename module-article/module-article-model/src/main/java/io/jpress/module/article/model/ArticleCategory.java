package io.jpress.module.article.model;

import io.jboot.db.annotation.Table;
import io.jboot.utils.StringUtils;
import io.jpress.module.article.model.base.BaseArticleCategory;

import java.util.ArrayList;
import java.util.List;

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
     * 用户自建分类，目前暂不考虑这部分的实现
     */
    public static final String TYPE_USER_CATEGORY = "user_category";


    public boolean isTag() {
        return TYPE_TAG.equals(getType());
    }

    /**
     * 是否是顶级菜单
     *
     * @return
     */
    public boolean isTopCategory() {
        return getPid() != null && getPid() == 0;
    }

    private List<ArticleCategory> childs;

    public List<ArticleCategory> getChilds() {
        return childs;
    }

    public void setChilds(List<ArticleCategory> childs) {
        this.childs = childs;
    }

    public void addChild(ArticleCategory child) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(child);
    }

    private int layerNO = 0;

    public int getLayerNO() {
        return layerNO;
    }

    public void setLayerNO(int layerNO) {
        this.layerNO = layerNO;
    }

    public String getLayerString() {
        if (layerNO == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layerNO; i++) {
            if (i == 0)
                sb.append("|—");
            else
                sb.append("—");
        }
        return sb.toString();
    }

    public boolean isMyChild(long id) {
        if (childs == null || childs.isEmpty()) {
            return false;
        }

        return isMyChild(childs, id);
    }

    private boolean isMyChild(List<ArticleCategory> categories, long id) {
        for (ArticleCategory category : categories) {
            if (category.getId() == id) {
                return true;
            }

            if (category.getChilds() != null) {
                boolean isChild = isMyChild(category.getChilds(), id);
                if (isChild) return true;
            }
        }
        return false;
    }

    public String getUrl(String suffix) {
        switch (getType()) {
            case TYPE_CATEGORY:
                return "/article/category/" + getSlug() + suffix;
            case TYPE_TAG:
                return "/article/tag/" + getSlug() + suffix;
        }
        return "";
    }


    public String getHtmlView() {
        return StringUtils.isBlank(getStyle()) ? "articlecategory.html" : "articlecategory_" + getStyle().trim() + ".html";
    }
}

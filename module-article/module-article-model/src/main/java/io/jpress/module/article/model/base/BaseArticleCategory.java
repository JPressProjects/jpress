package io.jpress.module.article.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jpress.base.BaseSortModel;

/**
 * Generated by JPress, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseArticleCategory<M extends BaseArticleCategory<M>> extends BaseSortModel<M> implements IBean {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
	public void setId(java.lang.Long id) {
		set("id", id);
	}

    /**
     * 主键ID
     */
	public java.lang.Long getId() {
		return getLong("id");
	}

    /**
     * 父级分类的ID
     */
	public void setPid(java.lang.Long pid) {
		set("pid", pid);
	}

    /**
     * 父级分类的ID
     */
	public java.lang.Long getPid() {
		return getLong("pid");
	}

    /**
     * 分类创建的用户ID
     */
	public void setUserId(java.lang.Long userId) {
		set("user_id", userId);
	}

    /**
     * 分类创建的用户ID
     */
	public java.lang.Long getUserId() {
		return getLong("user_id");
	}

    /**
     * slug
     */
	public void setSlug(java.lang.String slug) {
		set("slug", slug);
	}

    /**
     * slug
     */
	public java.lang.String getSlug() {
		return getStr("slug");
	}

    /**
     * 标题
     */
	public void setTitle(java.lang.String title) {
		set("title", title);
	}

    /**
     * 标题
     */
	public java.lang.String getTitle() {
		return getStr("title");
	}

    /**
     * 内容描述
     */
	public void setContent(java.lang.String content) {
		set("content", content);
	}

    /**
     * 内容描述
     */
	public java.lang.String getContent() {
		return getStr("content");
	}

    /**
     * 摘要
     */
	public void setSummary(java.lang.String summary) {
		set("summary", summary);
	}

    /**
     * 摘要
     */
	public java.lang.String getSummary() {
		return getStr("summary");
	}

    /**
     * 模板样式
     */
	public void setStyle(java.lang.String style) {
		set("style", style);
	}

    /**
     * 模板样式
     */
	public java.lang.String getStyle() {
		return getStr("style");
	}

    /**
     * 类型，比如：分类、tag、专题
     */
	public void setType(java.lang.String type) {
		set("type", type);
	}

    /**
     * 类型，比如：分类、tag、专题
     */
	public java.lang.String getType() {
		return getStr("type");
	}

    /**
     * 图标
     */
	public void setIcon(java.lang.String icon) {
		set("icon", icon);
	}

    /**
     * 图标
     */
	public java.lang.String getIcon() {
		return getStr("icon");
	}

    /**
     * 装饰图
     */
	public void setOrnament(java.lang.String ornament) {
		set("ornament", ornament);
	}

    /**
     * 装饰图
     */
	public java.lang.String getOrnament() {
		return getStr("ornament");
	}

    /**
     * 顶部装饰图
     */
	public void setTopBg(java.lang.String topBg) {
		set("top_bg", topBg);
	}

    /**
     * 顶部装饰图
     */
	public java.lang.String getTopBg() {
		return getStr("top_bg");
	}

    /**
     * 该分类的内容数量
     */
	public void setCount(java.lang.Long count) {
		set("count", count);
	}

    /**
     * 该分类的内容数量
     */
	public java.lang.Long getCount() {
		return getLong("count");
	}

    /**
     * 排序编码
     */
	public void setOrderNumber(java.lang.Integer orderNumber) {
		set("order_number", orderNumber);
	}

    /**
     * 排序编码
     */
	public java.lang.Integer getOrderNumber() {
		return getInt("order_number");
	}

    /**
     * 标识
     */
	public void setFlag(java.lang.String flag) {
		set("flag", flag);
	}

    /**
     * 标识
     */
	public java.lang.String getFlag() {
		return getStr("flag");
	}

    /**
     * SEO关键字
     */
	public void setMetaKeywords(java.lang.String metaKeywords) {
		set("meta_keywords", metaKeywords);
	}

    /**
     * SEO关键字
     */
	public java.lang.String getMetaKeywords() {
		return getStr("meta_keywords");
	}

    /**
     * SEO描述内容
     */
	public void setMetaDescription(java.lang.String metaDescription) {
		set("meta_description", metaDescription);
	}

    /**
     * SEO描述内容
     */
	public java.lang.String getMetaDescription() {
		return getStr("meta_description");
	}

    /**
     * 创建日期
     */
	public void setCreated(java.util.Date created) {
		set("created", created);
	}

    /**
     * 创建日期
     */
	public java.util.Date getCreated() {
		return getDate("created");
	}

    /**
     * 修改日期
     */
	public void setModified(java.util.Date modified) {
		set("modified", modified);
	}

    /**
     * 修改日期
     */
	public java.util.Date getModified() {
		return getDate("modified");
	}

}


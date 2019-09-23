package io.jpress.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JPress, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseMemberGroup<M extends BaseMemberGroup<M>> extends JbootModel<M> implements IBean {

    private static final long serialVersionUID = 1L;

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setIcon(java.lang.String icon) {
		set("icon", icon);
	}
	
	public java.lang.String getIcon() {
		return getStr("icon");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setSummary(java.lang.String summary) {
		set("summary", summary);
	}
	
	public java.lang.String getSummary() {
		return getStr("summary");
	}

	public void setThumbnail(java.lang.String thumbnail) {
		set("thumbnail", thumbnail);
	}
	
	public java.lang.String getThumbnail() {
		return getStr("thumbnail");
	}

	public void setVideo(java.lang.String video) {
		set("video", video);
	}
	
	public java.lang.String getVideo() {
		return getStr("video");
	}

	public void setOrderNumber(java.lang.Integer orderNumber) {
		set("order_number", orderNumber);
	}
	
	public java.lang.Integer getOrderNumber() {
		return getInt("order_number");
	}

	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}
	
	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public void setLimitedPrice(java.math.BigDecimal limitedPrice) {
		set("limited_price", limitedPrice);
	}
	
	public java.math.BigDecimal getLimitedPrice() {
		return get("limited_price");
	}

	public void setLimitedTime(java.util.Date limitedTime) {
		set("limited_time", limitedTime);
	}
	
	public java.util.Date getLimitedTime() {
		return get("limited_time");
	}

	public void setDistPrice(java.math.BigDecimal distPrice) {
		set("dist_price", distPrice);
	}
	
	public java.math.BigDecimal getDistPrice() {
		return get("dist_price");
	}

	public void setDistEnable(java.lang.Boolean distEnable) {
		set("dist_enable", distEnable);
	}
	
	public java.lang.Boolean getDistEnable() {
		return get("dist_enable");
	}

	public void setTermOfValidity(java.lang.Integer termOfValidity) {
		set("term_of_validity", termOfValidity);
	}
	
	public java.lang.Integer getTermOfValidity() {
		return getInt("term_of_validity");
	}

	public void setFlag(java.lang.String flag) {
		set("flag", flag);
	}
	
	public java.lang.String getFlag() {
		return getStr("flag");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public void setCreated(java.util.Date created) {
		set("created", created);
	}
	
	public java.util.Date getCreated() {
		return get("created");
	}

	public void setModified(java.util.Date modified) {
		set("modified", modified);
	}
	
	public java.util.Date getModified() {
		return get("modified");
	}

}

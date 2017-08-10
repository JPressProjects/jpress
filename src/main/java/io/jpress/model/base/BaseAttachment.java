
package io.jpress.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 *  Auto generated, do not modify this file.
 */
@SuppressWarnings("serial")
public class BaseAttachment<M extends BaseAttachment<M>> extends JbootModel<M> implements IBean {

	public static final String ACTION_ADD = "attachment:add";
	public static final String ACTION_DELETE = "attachment:delete";
	public static final String ACTION_UPDATE = "attachment:update";

	@Override
	protected String addAction() {
		return ACTION_ADD;
	}

	@Override
	protected String deleteAction() {
		return ACTION_DELETE;
	}

	@Override
	protected String updateAction() {
		return ACTION_UPDATE;
	}

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		return get("id");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setUserId(java.math.BigInteger userId) {
		set("user_id", userId);
	}

	public java.math.BigInteger getUserId() {
		return get("user_id");
	}

	public void setContentId(java.math.BigInteger contentId) {
		set("content_id", contentId);
	}

	public java.math.BigInteger getContentId() {
		return get("content_id");
	}

	public void setPath(java.lang.String path) {
		set("path", path);
	}

	public java.lang.String getPath() {
		return getStr("path");
	}

	public void setMimeType(java.lang.String mimeType) {
		set("mime_type", mimeType);
	}

	public java.lang.String getMimeType() {
		return getStr("mime_type");
	}

	public void setSuffix(java.lang.String suffix) {
		set("suffix", suffix);
	}

	public java.lang.String getSuffix() {
		return getStr("suffix");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return getStr("type");
	}

	public void setFlag(java.lang.String flag) {
		set("flag", flag);
	}

	public java.lang.String getFlag() {
		return getStr("flag");
	}

	public void setLat(java.math.BigDecimal lat) {
		set("lat", lat);
	}

	public java.math.BigDecimal getLat() {
		return get("lat");
	}

	public void setLng(java.math.BigDecimal lng) {
		set("lng", lng);
	}

	public java.math.BigDecimal getLng() {
		return get("lng");
	}

	public void setOrderNumber(java.lang.Integer orderNumber) {
		set("order_number", orderNumber);
	}

	public java.lang.Integer getOrderNumber() {
		return getInt("order_number");
	}

	public void setCreated(java.util.Date created) {
		set("created", created);
	}

	public java.util.Date getCreated() {
		return get("created");
	}

}

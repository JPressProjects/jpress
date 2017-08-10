
package io.jpress.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 *  Auto generated, do not modify this file.
 */
@SuppressWarnings("serial")
public class BaseMetadata<M extends BaseMetadata<M>> extends JbootModel<M> implements IBean {

	public static final String ACTION_ADD = "metadata:add";
	public static final String ACTION_DELETE = "metadata:delete";
	public static final String ACTION_UPDATE = "metadata:update";

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

	public void setMetaKey(java.lang.String metaKey) {
		set("meta_key", metaKey);
	}

	public java.lang.String getMetaKey() {
		return getStr("meta_key");
	}

	public void setMetaValue(java.lang.String metaValue) {
		set("meta_value", metaValue);
	}

	public java.lang.String getMetaValue() {
		return getStr("meta_value");
	}

	public void setObjectType(java.lang.String objectType) {
		set("object_type", objectType);
	}

	public java.lang.String getObjectType() {
		return getStr("object_type");
	}

	public void setObjectId(java.math.BigInteger objectId) {
		set("object_id", objectId);
	}

	public java.math.BigInteger getObjectId() {
		return get("object_id");
	}

}

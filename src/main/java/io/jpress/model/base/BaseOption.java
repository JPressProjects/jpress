
package io.jpress.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 *  Auto generated, do not modify this file.
 */
@SuppressWarnings("serial")
public class BaseOption<M extends BaseOption<M>> extends JbootModel<M> implements IBean {

	public static final String ACTION_ADD = "option:add";
	public static final String ACTION_DELETE = "option:delete";
	public static final String ACTION_UPDATE = "option:update";

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

	public void setOptionKey(java.lang.String optionKey) {
		set("option_key", optionKey);
	}

	public java.lang.String getOptionKey() {
		return getStr("option_key");
	}

	public void setOptionValue(java.lang.String optionValue) {
		set("option_value", optionValue);
	}

	public java.lang.String getOptionValue() {
		return getStr("option_value");
	}

}

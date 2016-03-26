package io.jpress.ui.function;

import io.jpress.core.ui.JFunction;
import io.jpress.model.Option;

public class OptionChecked extends JFunction {

	@Override
	public Object onExec() {
		String key = getToString(0);
		if (key == null)
			return "";

		if (key.startsWith("!")) {
			Boolean value = tryToGetBool(key.substring(1));
			if (value != null && !value) {
				return "checked=\"checked\"";
			}
		} else {
			Boolean value = tryToGetBool(key);
			if (value != null && value) {
				return "checked=\"checked\"";
			}
		}

		return "";
	}

	private Boolean tryToGetBool(String key) {
		String value = Option.cacheValue(key);
		Boolean ret = null;
		try {
			ret = Boolean.parseBoolean(value);
		} catch (Exception e) {
		}

		return ret;
	}

}

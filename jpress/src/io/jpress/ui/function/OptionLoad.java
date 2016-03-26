package io.jpress.ui.function;

import io.jpress.core.ui.JFunction;
import io.jpress.model.Option;


public class OptionLoad extends JFunction {
	

	@Override
	public Object onExec() {
		String key = getToString(0);
		return Option.findValue(key);
	}
	
}

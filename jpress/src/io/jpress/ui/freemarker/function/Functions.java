package io.jpress.ui.freemarker.function;

import io.jpress.core.Jpress;

public class Functions {

	public static void initInStarted() {
		Jpress.addFunction("taxonomyBox", new TaxonomyBox());
		Jpress.addFunction("option", new OptionCache());
		Jpress.addFunction("optionLoad", new OptionLoad());
		Jpress.addFunction("checked", new OptionChecked());
	}
}

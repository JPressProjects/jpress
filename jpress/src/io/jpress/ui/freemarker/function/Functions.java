package io.jpress.ui.freemarker.function;

import io.jpress.core.Jpress;

public class Functions {

	public static void init() {
		Jpress.addFunction("taxonomyBox", new TaxonomyBox());
		Jpress.addFunction("option", new OptionValue());
		Jpress.addFunction("checked", new OptionChecked());
	}
}

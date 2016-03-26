package io.jpress.plugin.target;

import java.util.ArrayList;
import java.util.List;

public class TargetConverterManager {

	List<ItargetConverter> converters = new ArrayList<ItargetConverter>();

	public void register(Class<? extends ItargetConverter> clazz) {
		for (ItargetConverter tc : converters) {
			if (tc.getClass() == clazz) {
				throw new RuntimeException(String.format(
						"Class [%s] has registered", clazz.getName()));
			}
		}

		try {
			converters.add(clazz.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ItargetConverter match(String target) {
		for (ItargetConverter converter : converters) {
			if (converter.match(target))
				return converter;
		}
		return null;
	}

}

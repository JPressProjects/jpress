package io.jpress.plugin.target;

import com.jfinal.plugin.IPlugin;

public class JTargetPlugin implements IPlugin {

	@Override
	public boolean start() {
		TargetKit.init(new TargetConverterManager());
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}

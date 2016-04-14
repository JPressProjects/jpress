package io.jpress.addon.helloworld;

import io.jpress.core.addon.Hooks;
import io.jpress.core.addon.IAddon;

public class HelloAddon implements IAddon {

	@Override
	public void onStart(Hooks hooks) {
		
		hooks.register(Hooks.HOOK_PROCESS_CONTROLLER, HelloHook.class);
		
		System.err.println("------->>>>>>HelloAddon started");
	}

	@Override
	public void onStop() {

	}

}

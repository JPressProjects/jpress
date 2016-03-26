package io.jpress.ui.tag;

import java.util.List;

import io.jpress.core.Jpress;
import io.jpress.core.ui.JTag;
import io.jpress.template.Module;
import io.jpress.template.Template;

public class ModuleTag extends JTag {

	@Override
	public void onRender() {
		
		Template t = Jpress.currentTemplate();
		
		if(t != null){
			List<Module> modules = t.getModules();
			setVariable("modules",modules);
		}
		
		renderBody();
	}

}

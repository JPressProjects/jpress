package io.jpress;

import io.jpress.core.Jpress;
import io.jpress.core.JpressConfig;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.ContentListener;
import io.jpress.plugin.message.listener.SettingChangedListener;
import io.jpress.plugin.message.listener.UserActionListener;
import io.jpress.plugin.target.TargetKit;
import io.jpress.plugin.target.converter.ContentTargetConverter;
import io.jpress.ui.function.OptionCache;
import io.jpress.ui.function.OptionChecked;
import io.jpress.ui.function.OptionLoad;
import io.jpress.ui.function.TaxonomyBox;
import io.jpress.ui.tag.ContentPageTag;
import io.jpress.ui.tag.ContentTag;
import io.jpress.ui.tag.MenuTag;
import io.jpress.ui.tag.ModuleTag;
import io.jpress.ui.tag.WidgetContainerTag;

public class Config extends JpressConfig {

	@Override
	public void onJfinalStarted() {
		
		{ //tags
			Jpress.setFreeMarkerSharedVariable("jp_clist", new ContentTag());
			Jpress.setFreeMarkerSharedVariable("jp_cpage", new ContentPageTag());
			Jpress.setFreeMarkerSharedVariable("jp_menu", new MenuTag());
			Jpress.setFreeMarkerSharedVariable("jp_module", new ModuleTag());
			Jpress.setFreeMarkerSharedVariable("jp_widgets", new WidgetContainerTag());
		}
		
		{ //functions
			Jpress.setFreeMarkerSharedVariable("taxonomyBox", new TaxonomyBox());
			Jpress.setFreeMarkerSharedVariable("option", new OptionCache());
			Jpress.setFreeMarkerSharedVariable("optionLoad", new OptionLoad());
			Jpress.setFreeMarkerSharedVariable("checked", new OptionChecked());
		}
		
		
		{ //target converters
			TargetKit.register(ContentTargetConverter.class);
		}
		
		
		{ //messageListeners
			MessageKit.register(ContentListener.class);
			MessageKit.register(UserActionListener.class);
			MessageKit.register(SettingChangedListener.class);
		}
		
		
	}


}

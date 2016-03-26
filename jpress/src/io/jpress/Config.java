/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress;

import io.jpress.core.Jpress;
import io.jpress.core.JpressConfig;
import io.jpress.core.ui.JWidgetContainer;
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

public class Config extends JpressConfig {

	@Override
	public void onJfinalStarted() {
		
		{ //tags
			Jpress.setFreeMarkerSharedVariable("jp_clist", new ContentTag());
			Jpress.setFreeMarkerSharedVariable("jp_cpage", new ContentPageTag());
			Jpress.setFreeMarkerSharedVariable("jp_menu", new MenuTag());
			Jpress.setFreeMarkerSharedVariable("jp_module", new ModuleTag());
			Jpress.setFreeMarkerSharedVariable("jp_widgets", new JWidgetContainer());
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

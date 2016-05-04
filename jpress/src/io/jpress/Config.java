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

import io.jpress.core.JpressConfig;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.ContentListener;
import io.jpress.plugin.message.listener.SettingChangedListener;
import io.jpress.plugin.message.listener.UserActionListener;
import io.jpress.router.RouterKit;
import io.jpress.router.converter.PageRouterConverter;
import io.jpress.router.converter.TaxonomyRouterConverter;
import io.jpress.ui.freemarker.function.Functions;
import io.jpress.ui.freemarker.tag.Tags;

public class Config extends JpressConfig {

	@Override
	public void onJfinalStarted() {

		Tags.initInStarted();
		Functions.initInStarted();

		{ // target converters
			RouterKit.register(TaxonomyRouterConverter.class);
			RouterKit.register(PageRouterConverter.class);
		}

		{ // messageListeners
			MessageKit.register(ContentListener.class);
			MessageKit.register(UserActionListener.class);
			MessageKit.register(SettingChangedListener.class);
		}

	}

}

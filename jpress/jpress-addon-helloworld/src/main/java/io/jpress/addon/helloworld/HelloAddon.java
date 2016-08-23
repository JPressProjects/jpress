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
package io.jpress.addon.helloworld;

import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.jfinal.render.TextRender;

import io.jpress.core.addon.Addon;
import io.jpress.core.addon.Hook;
import io.jpress.core.addon.Hooks;
import io.jpress.message.MessageKit;

public class HelloAddon extends Addon {

	/**
	 * AddonController 请求的钩子
	 * @param controller
	 */
	@Hook(Hooks.PROCESS_CONTROLLER)
	public Render hello(Controller controller) {
		// 访问 http://127.0.0.1:8080/addon 看到效果
		return new TextRender("hello addon");
	}

	@Override
	public boolean onStart() {
		MessageKit.register(HelloMessage.class);
		return true;
	}

	@Override
	public boolean onStop() {
		MessageKit.unRegister(HelloMessage.class);
		return true;
	}


}

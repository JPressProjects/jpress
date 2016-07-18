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
package io.jpress.front.controller;

import com.jfinal.render.Render;

import io.jpress.core.JBaseController;
import io.jpress.core.addon.HookInvoker;
import io.jpress.router.RouterMapping;

@RouterMapping(url = "/addon")
public class AddonController extends JBaseController {

	public void index() {
		Render render = HookInvoker.processController(this);
		if (null == render) {
			renderError(404);
			return;
		}

		render(render);
	}
}

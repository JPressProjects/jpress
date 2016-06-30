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
package io.jpress.controller.front;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@SuppressWarnings("unused")
@RouterMapping(url = "/api")
public class ApiController extends JBaseController {

	public void index() {
		corsSetting();

		Boolean isOpen = OptionQuery.findValueAsBool("api_enable");
		if (isOpen == null || isOpen == false) {
			renderAjaxResult("api is not open", 1);
			return;
		}

		String appkey = getPara("appkey");
		if (!StringUtils.isNotBlank(appkey)) {
			renderAjaxResultForError("appkey must not empty!");
			return;
		}

		Content content = ContentQuery.findFirstByModuleAndText(Consts.MODULE_API_APPLICATION, appkey);
		if (content == null) {
			renderAjaxResultForError("appkey is error!");
			return;
		}

		String appSecret = content.getFlag();

		String sign = getPara("sign");
		if (!StringUtils.isNotBlank(sign)) {
			renderAjaxResultForError("sign must not empty!");
			return;
		}

		String sign_method = getPara("sign_method");
		if (!StringUtils.isNotBlank(sign_method)) {
			renderAjaxResultForError("sign_method must not empty!");
			return;
		}

		String method = getPara("method");
		if (!StringUtils.isNotBlank(method)) {
			renderAjaxResultForError("method must not empty!");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> oParams = getParaMap();
		if (oParams != null) {
			for (Map.Entry<String, String[]> entry : oParams.entrySet()) {
				String value = entry.getValue() == null ? "" : (entry.getValue()[0] == null ? "" : entry.getValue()[0]);
				params.put(entry.getKey(), value);
			}
		}
		params.remove("sign");

		String mySign = EncryptUtils.signForRequest(params, appSecret);
		if (!sign.equals(mySign)) {
			renderAjaxResultForError("sign is error!");
			return;
		}

		try {
			invoke(method);
		} catch (NoSuchMethodException e) {
			renderAjaxResultForError("hava no this method : " + method);
			return;
		} catch (Throwable e) {
			renderAjaxResultForError("system error!");
			return;
		}
	}

	private void corsSetting() {
		getResponse().setHeader("Access-Control-Allow-Origin", "*");
		getResponse().setHeader("Access-Control-Allow-Methods", "GET,POST");
	}

	private void invoke(String methodName) throws NoSuchMethodException, Throwable {
		Method method = ApiController.class.getDeclaredMethod(methodName);
		if (method == null) {
			throw new NoSuchMethodException();
		}
		method.setAccessible(true);
		method.invoke(this);
	}

	/////////////////////// api methods////////////////////////////
	private void test() {
		renderText("test ok!");
	}

}

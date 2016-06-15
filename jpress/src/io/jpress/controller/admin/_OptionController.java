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
package io.jpress.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseCRUDController;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Option;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/option", viewPath = "/WEB-INF/admin/option")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _OptionController extends JBaseCRUDController<User> {

	public void index() {
		render((getPara() == null ? "web" : getPara()) + ".html");
	}

	@Before(UCodeInterceptor.class)
	public void save() {

		List<UploadFile> fileList = null;
		if (isMultipartRequest()) {
			fileList = getFiles();
		}

		List<String> keyList = new ArrayList<String>();
		Map<String, String[]> paraMap = getParaMap();
		if (paraMap != null && !paraMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : paraMap.entrySet()) {
				if (entry.getValue() != null && entry.getValue().length > 0) {
					keyList.add(entry.getKey());
					autoSave(entry.getKey(), entry.getValue()[0], fileList);
				}
			}
		}

		String autosaveString = getPara("autosave");
		if (StringUtils.isNotBlank(autosaveString)) {
			String[] keys = autosaveString.split(",");
			if (keys != null && keys.length > 0) {
				for (String key : keys) {
					if (StringUtils.isNotBlank(key)) {
						key = key.trim();
						keyList.add(key);
						autoSave(key, getRequest().getParameter(key), fileList);
					}
				}
			}
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, keyList);
		renderAjaxResultForSuccess();
	}

	private void autoSave(String key, String value, List<UploadFile> fileList) {
		if (fileList != null && fileList.size() > 0) {
			for (UploadFile ufile : fileList) {
				if (key.equals(ufile.getParameterName())) {
					value = AttachmentUtils.moveFile(ufile);
					value = value.replace("\\", "/");
				}
			}
		}

		if ("".equals(value)) {
			value = null;
		}

		Option.saveOrUpdate(key, value);
	}

}

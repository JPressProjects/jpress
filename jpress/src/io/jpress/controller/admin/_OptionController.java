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

import java.util.List;

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

		String autosaveString = getPara("autosave");
		if (autosaveString == null || "".equals(autosaveString.trim())) {
			renderAjaxResultForError("there is nothing to save.");
			return;
		}

		String[] keys = autosaveString.split(",");
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				String value = null;
				if (fileList != null && fileList.size() > 0) {
					for (UploadFile ufile : fileList) {
						if (key.equals(ufile.getParameterName())) {
							value = AttachmentUtils.moveFile(ufile);
						}
					}
				}
				if (value == null) {
					value = getPara(key, "");
				}else{
					value = value.replace("\\", "/");
				}
				Option.saveOrUpdate(key, value);
			}
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, keys);
		renderAjaxResultForSuccess("save ok");
	}

}

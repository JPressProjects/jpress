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
package io.jpress.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.query.OptionQuery;
import io.jpress.plugin.message.Actions;
import io.jpress.plugin.message.MessageKit;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/option", viewPath = "/WEB-INF/admin/option")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _OptionController extends JBaseController {

	public void index() {
		render((getPara() == null ? "web" : getPara()) + ".html");
	}

	@Before(UCodeInterceptor.class)
	public void save() {

		List<UploadFile> fileList = null;
		if (isMultipartRequest()) {
			fileList = getFiles();
		}
		
		HashMap<String, String> filesMap = new HashMap<String, String>();
		if(fileList != null){
			for(UploadFile ufile : fileList){
				String filePath = AttachmentUtils.moveFile(ufile).replace("\\", "/");
				filesMap.put(ufile.getParameterName(), filePath);
			}
		}

		List<String> keyList = new ArrayList<String>();
		Map<String, String[]> paraMap = getParaMap();
		if (paraMap != null && !paraMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : paraMap.entrySet()) {
				if (entry.getValue() != null && entry.getValue().length > 0) {
					if(StringUtils.isNotBlank(entry.getKey()) && !"autosave".equals(entry.getKey())){
						keyList.add(entry.getKey());
						doSave(entry.getKey(), entry.getValue()[0], filesMap);
					}
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
						doSave(key, getRequest().getParameter(key), filesMap);
					}
				}
			}
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, keyList);
		renderAjaxResultForSuccess();
	}

	private void doSave(String key, String value,HashMap<String, String> filesMap) {
		
		if(filesMap.containsKey(key)){
			value = filesMap.get(key); //有相同的key的情况下，以上传的文件为准。
		}

		if ("".equals(value)) {
			value = null;
		}

		OptionQuery.me().saveOrUpdate(key, value);
	}

}

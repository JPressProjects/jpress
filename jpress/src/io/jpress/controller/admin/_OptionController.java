package io.jpress.controller.admin;

import java.util.List;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Option;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.AttachmentUtils;

import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;

@UrlMapping(url = "/admin/option", viewPath = "/WEB-INF/admin/option")
public class _OptionController extends BaseAdminController<User> {

	public void index() {
		keepPara();
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
				}
				Option.saveOrUpdate(key, value);
			}
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, keys);
		renderAjaxResultForSuccess("save ok");
	}

}

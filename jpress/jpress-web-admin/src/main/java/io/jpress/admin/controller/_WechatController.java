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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.ReturnCode;

import io.jpress.Consts;
import io.jpress.core.JBaseCRUDController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.model.query.ContentQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.utils.StringUtils;
import io.jpress.wechat.WechatApi;
import io.jpress.wechat.WechatApiConfigInterceptor;
import io.jpress.wechat.WechatConsts;

@RouterMapping(url = "/admin/wechat", viewPath = "/WEB-INF/admin/wechat")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _WechatController extends JBaseCRUDController<Content> {

	private String getStatus() {
		return getPara("s");
	}

	@Override
	public Page<Content> onIndexDataLoad(int pageNumber, int pageSize) {
		if (getStatus() != null && !"".equals(getStatus().trim())) {
			return ContentQuery.me().paginateByModuleAndStatus(pageNumber, pageSize, Consts.MODULE_WECHAT_REPLY,
					getStatus());
		}
		return ContentQuery.me().paginateByModuleInNormal(pageNumber, pageSize, Consts.MODULE_WECHAT_REPLY);
	}

	public void reply_default() {

	}

	public void reply_advanced() {

	}

	public void option() {
		setAttr("modules", TemplateManager.me().currentTemplateModules());
	}

	public void menu() {
		List<Content> list = ContentQuery.me().findByModule(Consts.MODULE_WECHAT_MENU, null, "order_number ASC");
		ModelSorter.sort(list);

		List<Content> wechat_menulist = new ArrayList<Content>();
		wechat_menulist.addAll(list);

		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			Content c = ContentQuery.me().findById(id);
			setAttr("wechat_menu", c);

			setAttr(c.getFlag() + "_selected", "selected=\"selected\"");

			if (id != null && list != null) {
				ModelSorter.removeTreeBranch(list, id);
			}
		}

		setAttr("wechat_menus", list);
		setAttr("wechat_menulist", wechat_menulist);
	}

	@Before(UCodeInterceptor.class)
	public void menuSave() {
		Content c = getModel(Content.class);

		if (StringUtils.isBlank(c.getTitle())) {
			renderAjaxResultForError("名称不能为空！");
			return;
		}

		if (StringUtils.isBlank(c.getText())) {
			renderAjaxResultForError("关键字不能为空！");
			return;
		}

		if (c.getParentId() == null) {
			long count = ContentQuery.me().findCountInNormalByParentId(null, Consts.MODULE_WECHAT_MENU);
			if (count > 3) {
				renderAjaxResultForError("顶级菜单不能超过3个！");
				return;
			}
		} else {
			long count = ContentQuery.me().findCountInNormalByParentId(null, Consts.MODULE_WECHAT_MENU);
			if (count > 5) {
				renderAjaxResultForError("子级菜单不能超过5个！");
				return;
			}
		}

		c.setStatus(Content.STATUS_NORMAL);
		c.setModule(Consts.MODULE_WECHAT_MENU);
		c.setModified(new Date());
		if (c.getCreated() == null) {
			c.setCreated(new Date());
		}
		c.saveOrUpdate();
		renderAjaxResultForSuccess();
	}

	@Before(UCodeInterceptor.class)
	public void menuDel() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			if (ContentQuery.me().deleteById(id)) {
				renderAjaxResultForSuccess();
			}
		}
		renderAjaxResultForSuccess();
	}

	@Before(WechatApiConfigInterceptor.class)
	public void menuSync() {
		List<Content> wechatMenus = ContentQuery.me().findByModule(Consts.MODULE_WECHAT_MENU, null, "order_number ASC");
		ModelSorter.tree(wechatMenus);

		if (wechatMenus != null) {
			JSONArray button = new JSONArray();
			for (Content content : wechatMenus) {
				if (content.hasChild()) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", content.getTitle());
					List<Content> childMenus = content.getChildList();
					JSONArray sub_buttons = new JSONArray();
					for (Content c : childMenus) {
						JSONObject sub_button = new JSONObject();
						sub_button.put("type", c.getFlag());
						sub_button.put("name", c.getTitle());

						if ("view".equals(c.getFlag())) {
							sub_button.put("url", c.getText());
						} else {
							sub_button.put("key", c.getText());
						}

						sub_buttons.add(sub_button);
					}
					jsonObject.put("sub_button", sub_buttons);
					button.add(jsonObject);
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("type", content.getFlag());
					jsonObject.put("name", content.getTitle());

					if ("view".equals(content.getFlag())) {
						jsonObject.put("url", content.getText());
					} else {
						jsonObject.put("key", content.getText());
					}

					button.add(jsonObject);
				}
			}

			JSONObject wechatMenuJson = new JSONObject();
			wechatMenuJson.put("button", button);
			String jsonString = wechatMenuJson.toJSONString();

			ApiResult result = WechatApi.createMenu(jsonString);
			if (result.isSucceed()) {
				renderAjaxResultForSuccess();
			} else {
				String message = WechatConsts.getErrorMessage(result.getErrorCode());
				if (StringUtils.isBlank(message)) {
					message = ReturnCode.get(result.getErrorCode());
				}
				if (StringUtils.isBlank(message)) {
					message = "未知错误";
				}
				renderAjaxResult(message, result.getErrorCode());
			}
		} else {
			renderAjaxResultForError("还没有添加菜单信息");
		}
	}

	@Before(UCodeInterceptor.class)
	public void trash() {
		BigInteger id = getParaToBigInteger("id");
		Content c = ContentQuery.me().findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_DELETE);
			c.saveOrUpdate();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void batchTrash() {
		BigInteger[] ids = getParaValuesToBigInteger("dataItem");
		int count = ContentQuery.me().batchTrash(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		Content c = ContentQuery.me().findById(id);
		if (c != null && c.isDelete()) {
			c.delete();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("restore error!");
		}
	}

	@Override
	public void edit() {

		String id = getPara("id");
		if (id != null) {
			setAttr("content", ContentQuery.me().findById(new BigInteger(id)));
		} else {
			Content c = new Content();
			c.setCreated(new Date());
			setAttr("content", c);
		}

		render("edit.html");
	}

	@Before(UCodeInterceptor.class)
	public void save() {
		Content content = getModel(Content.class);

		content.saveOrUpdate();
		renderAjaxResultForSuccess();
	}

}

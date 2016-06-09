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

import java.math.BigInteger;
import java.util.List;

import com.jfinal.aop.Before;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin/api")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ApiController extends JBaseController {

	public void index() {
		BigInteger id = getParaToBigInteger("id");
		if (null != id) {
			setAttr("content", Content.DAO.findById(id));
		}
		List<Content> contents = Content.DAO.findByModule("apiApplication");
		setAttr("contents", contents);
		render("/WEB-INF/admin/option/api.html");
	}

	public void save() {

		Boolean apiEnable = getParaToBoolean("api_enable", Boolean.FALSE);
		Option.saveOrUpdate("api_enable", apiEnable.toString());

		Content c = getModel(Content.class);
		
		if(StringUtils.areNotBlank(c.getTitle(),c.getText(),c.getFlag())){
			c.saveOrUpdate();
		}
		
		renderAjaxResultForSuccess();
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			Content.DAO.deleteById(id);
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError();
		}
	}
}

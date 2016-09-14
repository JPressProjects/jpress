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
import java.util.List;

import com.jfinal.aop.Before;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/api")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ApiController extends JBaseController {

	public void index() {
		BigInteger id = getParaToBigInteger("id");
		if (null != id) {
			setAttr("content", ContentQuery.me().findById(id));
		}
		List<Content> contents = ContentQuery.me().findByModule(Consts.MODULE_API_APPLICATION);
		setAttr("contents", contents);
		render("/WEB-INF/admin/option/api.html");
	}

	public void save() {

		Boolean apiEnable = getParaToBoolean("api_enable", Boolean.FALSE);
		OptionQuery.me().saveOrUpdate("api_enable", apiEnable.toString());
		
		Boolean apiCorsEnable = getParaToBoolean("api_cors_enable", Boolean.FALSE);
		OptionQuery.me().saveOrUpdate("api_cors_enable", apiCorsEnable.toString());

		Content c = getModel(Content.class);
		c.setModule(Consts.MODULE_API_APPLICATION);
		
		if(StringUtils.areNotBlank(c.getTitle(),c.getText(),c.getFlag())){
			c.saveOrUpdate();
		}
			
		renderAjaxResultForSuccess();
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			ContentQuery.me().deleteById(id);
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError();
		}
	}
}

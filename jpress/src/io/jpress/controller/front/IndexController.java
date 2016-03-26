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

import io.jpress.core.annotation.UrlMapping;

@UrlMapping(url = "/")
public class IndexController extends BaseFrontController{
	
	//c=? 访问页面
	//t=? 访问分类
	
	// https://www.xxx.com/module/slug-1.html   详情
	//                    /module/type/slug-2.html 列表
	
	//  例如：http://www.xxx.com/aritcle/slug.html
	//  例如：http://www.xxx.com/forum/slug.html
	//  例如：http://www.xxx.com/qa/slug.html
	
	//  例如：http://www.xxx.com/aritcle/tag/slug.html
	//  例如：http://www.xxx.com/aritcle/category/slug.html
	//  例如：http://www.xxx.com/aritcle/feature/slug-1.html
	
	//  例如：http://www.xxx.com/forum/tag/slug-1.html
	//  例如：http://www.xxx.com/forum/category/slug-2.html
	
	public void index() {
		render("index.html");
	}
	
	
	public void captcha(){
		renderCaptcha();
	}
	
	/**
	 * process sitemap.xml
	 */
	public void sitemap(){
		
	}
	
	
}

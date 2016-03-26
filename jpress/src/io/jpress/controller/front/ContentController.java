/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (michael@jpress.io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.controller.front;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Content;

@UrlMapping(url = "/c")
public class ContentController extends BaseFrontController{
	
	//  http://www.xxx.com/c/123.html   content.id:123  page:1
	//  http://www.xxx.com/c/123-1.html
	
	// http://www.xxx.com/c/abc.html content.slug:abc  page:1
	// http://www.xxx.com/c/abc-1.html
	
	public void index() {
		Content content = tryToGetContent();
		if( null == content){
			renderError(404);
			return;
		}
		
		setAttr("content", content);
		render(String.format("content_%s_%s.html", content.getModule(), content.getStyle()));
	}


	public Content tryToGetContent() {
		long id = getParaToLong(0, (long)0);
		return id > 0 ? Content.DAO.findById(id) : Content.DAO.findBySlug(getPara(0));
	}
	
	
}

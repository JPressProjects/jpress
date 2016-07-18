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
package io.jpress.menu;

import com.jfinal.core.JFinal;

public class MenuItem {

	private String id;
	private String url;
	private String text;

	public MenuItem() {
	}

	public MenuItem(String id, String url, String text) {
		this.id = id;
		this.url = url;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String generateHtml() {
		return String.format("<li id=\"%s\"><a href=\"%s\">%s</a></li>", id, JFinal.me().getContextPath()+url, text);
	}

	@Override
	public String toString() {
		return "MenuItem [id=" + id + ", url=" + url + ", text=" + text + "]";
	}

}

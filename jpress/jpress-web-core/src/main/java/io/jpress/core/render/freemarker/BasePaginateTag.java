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
package io.jpress.core.render.freemarker;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public abstract class BasePaginateTag extends JTag {

	final Page<?> page;

	private String previousClass;
	private String nextClass;
	private String activeClass;
	private String disabledClass;
	private String anchor;
	private boolean onlyShowPreviousAndNext = false;

	public BasePaginateTag(Page<?> page) {
		this.page = page;
	}

	@Override
	public void onRender() {

		previousClass = getParam("previousClass", "previous");
		nextClass = getParam("nextClass", "next");
		activeClass = getParam("activeClass", "active");
		disabledClass = getParam("disabledClass", "disabled");
		anchor = getParam("anchor");

		onlyShowPreviousAndNext = getParamToBool("onlyShowPreviousAndNext", false);

		String previousText = getParam("previousText", "上一页");
		String nextText = getParam("nextText", "下一页");

		int currentPage = page.getPageNumber();
		int totalPage = page.getTotalPage();

		if ((totalPage <= 0) || (currentPage > totalPage)) {
			return;
		}

		int startPage = currentPage - 4;
		if (startPage < 1) {
			startPage = 1;
		}
		int endPage = currentPage + 4;
		if (endPage > totalPage) {
			endPage = totalPage;
		}

		if (currentPage <= 8) {
			startPage = 1;
		}

		if ((totalPage - currentPage) < 8) {
			endPage = totalPage;
		}

		List<PaginateItem> pages = new ArrayList<BasePaginateTag.PaginateItem>();
		if (currentPage == 1) {
			pages.add(new PaginateItem(previousClass + " " + disabledClass, "javascript:;", previousText));
		} else {
			pages.add(new PaginateItem(previousClass, getUrl(currentPage - 1), previousText));
		}

		if (currentPage > 8 && !onlyShowPreviousAndNext) {
			pages.add(new PaginateItem("", getUrl(1), "1"));
			pages.add(new PaginateItem("", getUrl(2), "2"));
			pages.add(new PaginateItem(disabledClass, "javascript:;", "..."));
		}

		if (!onlyShowPreviousAndNext) {
			for (int i = startPage; i <= endPage; i++) {
				if (currentPage == i) {
					pages.add(new PaginateItem(activeClass, "javascript:;", i));
				} else {
					pages.add(new PaginateItem("", getUrl(i), i));
				}
			}
		}

		if ((totalPage - currentPage) >= 8 && !onlyShowPreviousAndNext) {
			pages.add(new PaginateItem(disabledClass, "javascript:;", "..."));
			pages.add(new PaginateItem("", getUrl(totalPage - 1), totalPage - 1));
			pages.add(new PaginateItem("", getUrl(totalPage), totalPage));
		}

		if (currentPage == totalPage) {
			pages.add(new PaginateItem(nextClass + " " + disabledClass, "javascript:;", nextText));
		} else {
			pages.add(new PaginateItem(nextClass, getUrl(currentPage + 1), nextText));
		}

		setVariable("pages", pages);
		renderBody();
	}

	protected static boolean enalbleFakeStatic() {
		Boolean fakeStaticEnable = OptionQuery.me().findValueAsBool("router_fakestatic_enable");
		return fakeStaticEnable != null && fakeStaticEnable == true;
	}

	protected static String getFakeStaticSuffix() {
		String fakeStaticSuffix = OptionQuery.me().findValue("router_fakestatic_suffix");
		if (StringUtils.isNotBlank(fakeStaticSuffix)) {
			return fakeStaticSuffix.trim();
		}
		return ".html";
	}

	public Page<?> getPage() {
		return page;
	}

	public String getPrevious() {
		return previousClass;
	}

	public String getNext() {
		return nextClass;
	}

	public String getDisabled() {
		return disabledClass;
	}

	public String getAnchor() {
		return anchor;
	}

	protected abstract String getUrl(int pageNumber);

	public static class PaginateItem {
		private String style;
		private String url;
		private String text;

		public PaginateItem(String style, String url, String text) {
			this.style = style;
			this.url = url;
			this.text = text;
		}

		public PaginateItem(String style, String url, int text) {
			this.style = style;
			this.url = url;
			this.text = text + "";
		}

		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
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
	}

}

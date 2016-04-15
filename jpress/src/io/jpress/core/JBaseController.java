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
package io.jpress.core;

import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.i18n.Res;

public class JBaseController extends Controller {
	private static final char URL_PARA_SEPARATOR = JFinal.me().getConstants().getUrlParaSeparator().toCharArray()[0];
	private JSession session;

	public JBaseController() {
		session = new JSession(this);
	}

	@Override
	public String getPara(String name) {
		String result = getRequest().getParameter(name);
		if (null != result) {
			return Jsoup.clean(result, Whitelist.relaxed());
		}
		return null;
	}

	@Override
	public String getPara(String name, String defaultValue) {
		String result = getRequest().getParameter(name);
		if (null != result) {
			return Jsoup.clean(result, Whitelist.relaxed());
		}
		return defaultValue;
	}

	private int mParaCount = -1;

	public int getParaCount() {
		if (mParaCount != -1)
			return mParaCount;
		int mParaCount = 0;
		char[] parachars = getPara() == null ? null : getPara().toCharArray();
		if (parachars != null) {
			mParaCount = 1;
			for (char c : parachars) {
				if (URL_PARA_SEPARATOR == c) {
					mParaCount++;
				}
			}
		}
		return mParaCount;
	}

	public boolean isAjaxRequest() {
		String header = getRequest().getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(header);
	}

	public boolean isMultipartRequest() {
		String contentType = getRequest().getContentType();
		return contentType != null && contentType.toLowerCase().indexOf("multipart") != -1;
	}

	protected int getPageNumbere() {
		int page = getParaToInt("page", 1);
		if (page < 1) {
			page = 1;
		}
		return page;
	}

	protected int getPageSize() {
		int size = getParaToInt("size", 10);
		if (size < 1) {
			size = 1;
		}
		return size;
	}

	public void setHeader(String key, String value) {
		getResponse().setHeader(key, value);
	}

	public Res getI18nRes() {
		// Attribute set in JI18nInterceptor.class
		return getAttr("i18n");
	}

	public String getI18nValue(String key) {
		return getI18nRes().get(key);
	}

	@Before(NotAction.class)
	public void renderAjaxResultForSuccess() {
		renderAjaxResult("success", 0, null);
	}

	public void renderAjaxResultForSuccess(String message) {
		renderAjaxResult(message, 0, null);
	}

	@Before(NotAction.class)
	public void renderAjaxResultForError() {
		renderAjaxResult("error", 1, null);
	}

	public void renderAjaxResultForError(String message) {
		renderAjaxResult(message, 1, null);
	}

	public void renderAjaxResult(String message, int errorCode) {
		renderAjaxResult(message, errorCode, null);
	}

	public void renderAjaxResult(String message, int errorCode, Object data) {
		AjaxResult ar = new AjaxResult();
		ar.setMessage(message);
		ar.setErrorCode(errorCode);
		ar.setData(data);
		renderJson(ar);
	}

	@Override
	@Before(NotAction.class)
	public void createToken() {
		createToken("jtoken");
	}

	@Override
	public boolean validateToken() {
		return validateToken("jtoken");
	}

	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return getSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSessionAttr(String key) {
		return (T) session.getAttribute(key);
	}

	@Override
	public Controller setSessionAttr(String key, Object value) {
		session.setAttribute(key, value);
		return this;
	}

	@Override
	public Controller removeSessionAttr(String key) {
		session.removeAttribute(key);
		return this;
	}

	@Override
	@Before(NotAction.class)
	public void renderCaptcha() {
		render(new JCaptchaRender(this));
	}

	@Override
	public boolean validateCaptcha(String paraName) {
		return JCaptchaRender.validate(this, getPara(paraName));
	}

}

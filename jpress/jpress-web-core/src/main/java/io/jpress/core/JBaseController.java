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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.i18n.Res;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;

import io.jpress.Consts;
import io.jpress.core.render.AjaxResult;
import io.jpress.core.render.JCaptchaRender;
import io.jpress.model.User;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.JsoupUtils;
import io.jpress.utils.RequestUtils;
import io.jpress.utils.StringUtils;

public class JBaseController extends Controller {
	private static final char URL_PARA_SEPARATOR = JFinal.me().getConstants().getUrlParaSeparator().toCharArray()[0];
	private JSession session;

	public JBaseController() {
		session = new JSession(this);
	}

	@Override
	public String getPara(String name) {
		return JsoupUtils.clear(getRequest().getParameter(name));
	}

	@Override
	public String getPara(String name, String defaultValue) {
		String html = getRequest().getParameter(name);
		if (null != html) {
			return JsoupUtils.clear(html);
		}
		return defaultValue;
	}

	private int mParaCount = -1;

	public int getParaCount() {
		if (mParaCount != -1)
			return mParaCount;

		mParaCount = 0;
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

	/**
	 * 是否是手机浏览器
	 * 
	 * @return
	 */
	public boolean isMoblieBrowser() {
		return RequestUtils.isMoblieBrowser(getRequest());
	}

	/**
	 * 是否是微信浏览器
	 * 
	 * @return
	 */
	public boolean isWechatBrowser() {
		return RequestUtils.isWechatBrowser(getRequest());
	}

	/**
	 * 是否是IE浏览器
	 * 
	 * @return
	 */
	public boolean isIEBrowser() {
		return RequestUtils.isIEBrowser(getRequest());
	}

	public boolean isAjaxRequest() {
		return RequestUtils.isAjaxRequest(getRequest());
	}

	public boolean isMultipartRequest() {
		return RequestUtils.isMultipartRequest(getRequest());
	}

	protected int getPageNumber() {
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

	public void renderAjaxResultForSuccess(String message, Object data) {
		renderAjaxResult(message, 0, data);
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

		if (isIEBrowser()) {
			render(new JsonRender(ar).forIE());
		} else {
			renderJson(ar);
		}
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

	public BigInteger[] getParaValuesToBigInteger(String name) {
		String[] values = getRequest().getParameterValues(name);
		if (values == null)
			return null;
		BigInteger[] result = new BigInteger[values.length];
		for (int i = 0; i < result.length; i++)
			result[i] = new BigInteger(values[i]);
		return result;
	}

	public BigInteger getParaToBigInteger() {
		return toBigInteger(getPara(), null);
	}

	public BigInteger getParaToBigInteger(int index) {
		return toBigInteger(getPara(index), null);
	}

	public BigInteger getParaToBigInteger(int index, BigInteger defaultValue) {
		return toBigInteger(getPara(index), defaultValue);
	}

	public BigInteger getParaToBigInteger(String name) {
		return toBigInteger(getRequest().getParameter(name), null);
	}

	public BigInteger getParaToBigInteger(String name, BigInteger defaultValue) {
		return toBigInteger(getRequest().getParameter(name), defaultValue);
	}

	private BigInteger toBigInteger(String value, BigInteger defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return new BigInteger(value).negate();
			return new BigInteger(value);
		} catch (Exception e) {
			throw new ActionException(404, "Can not parse the parameter \"" + value + "\" to BigInteger value.");
		}
	}

	@Before(NotAction.class)
	public String getIPAddress() {
		return RequestUtils.getIpAddress(getRequest());
	}

	public User getLoginedUser() {
		return getAttr(Consts.ATTR_USER);
	}

	@Before(NotAction.class)
	public String getUserAgent() {
		return RequestUtils.getUserAgent(getRequest());
	}

	public Map<String, String> getMetas(Map<String, String> filesMap) {
		HashMap<String, String> metas = null;
		Map<String, String[]> requestMap = getParaMap();
		if (requestMap != null && !requestMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith("meta:")) {
					if (metas == null) {
						metas = new HashMap<String, String>();
					}
					String value = null;
					for (String v : entry.getValue()) {
						if (StringUtils.isNotEmpty(v)) {
							value = v;
							break;
						}
					}
					metas.put(key.substring(5), value);
				}
			}
		}

		if (filesMap != null) {
			for (Map.Entry<String, String> entry : filesMap.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith("meta:")) {
					if (metas == null) {
						metas = new HashMap<String, String>();
					}
					metas.put(key.substring(5), entry.getValue());
				}
			}
		}

		return metas;
	}

	public Map<String, String> getMetas() {
		return getMetas(getUploadFilesMap());
	}

	public HashMap<String, String> getUploadFilesMap() {
		List<UploadFile> fileList = null;
		if (isMultipartRequest()) {
			fileList = getFiles();
		}

		HashMap<String, String> filesMap = null;
		if (fileList != null) {
			filesMap = new HashMap<String, String>();
			for (UploadFile ufile : fileList) {
				String filePath = AttachmentUtils.moveFile(ufile).replace("\\", "/");
				filesMap.put(ufile.getParameterName(), filePath);
			}
		}
		return filesMap;
	}

}

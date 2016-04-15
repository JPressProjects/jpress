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

import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.ehcache.CacheKit;

@SuppressWarnings("deprecation")
public class JSession implements HttpSession {
	final Controller controller;
	private static final int TIME = 60 * 60 * 24 * 7;

	public JSession(Controller controller) {
		this.controller = controller;
	}

	private void doPut(String key, Object value) {
		CacheKit.put("session", key + tryToGetJsessionId(), value);
	}

	private void doRemove(String key) {
		CacheKit.remove("session", key + tryToGetJsessionId());
	}

	private Object doGet(String key) {
		return CacheKit.get("session", key + tryToGetJsessionId());
	}

	private String tryToGetJsessionId() {
		String sessionid = controller.getCookie("JPSESSIONID");
		if (sessionid == null || "".equals(sessionid.trim())) {
			sessionid = UUID.randomUUID().toString().replace("-", "");
			controller.setCookie("JPSESSIONID", sessionid, TIME, true);
		}
		return sessionid;
	}

	@Override
	public Object getAttribute(String key) {
		return doGet(key);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new RuntimeException("getAttributeNames method not finished.");
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return tryToGetJsessionId();
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return JFinal.me().getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new RuntimeException("getAttributeNames method not finished.");
	}

	@Override
	public Object getValue(String key) {
		return doGet(key);
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// do nothing
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void putValue(String key, Object value) {
		doPut(key, value);
	}

	@Override
	public void removeAttribute(String key) {
		doRemove(key);
	}

	@Override
	public void removeValue(String key) {
		doRemove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		doPut(key, value);
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
		// do noting,default 60 mins
	}

}

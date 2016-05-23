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

import java.util.List;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;

import io.jpress.core.annotation.Table;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.core.dialect.DbDialect;
import io.jpress.core.dialect.DbDialectFactory;
import io.jpress.core.render.JErrorRenderFactory;
import io.jpress.core.render.JpressRenderFactory;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.interceptor.GlobalInterceptor;
import io.jpress.interceptor.HookInterceptor;
import io.jpress.interceptor.JI18nInterceptor;
import io.jpress.plugin.message.MessagePlugin;

public abstract class JpressConfig extends JFinalConfig {

	public void configConstant(Constants constants) {
		PropKit.use("jpress.properties");

		constants.setDevMode(PropKit.getBoolean("dev_mode", false));
		constants.setViewType(ViewType.FREE_MARKER);
		constants.setI18nDefaultBaseName("language");
		constants.setErrorRenderFactory(new JErrorRenderFactory());
		constants.setBaseUploadPath("attachment");
		constants.setEncoding("utf-8");
		constants.setMainRenderFactory(new JpressRenderFactory());

		// constants.setTokenCache(new JTokenCache());
	}

	@SuppressWarnings("unchecked")
	public void configRoute(Routes routes) {
		List<Class<Controller>> controllerClassList = ClassScaner.scanSubClass(Controller.class);
		if (controllerClassList != null) {
			for (Class<?> clazz : controllerClassList) {
				UrlMapping urlMapping = clazz.getAnnotation(UrlMapping.class);
				if (null != urlMapping && null != urlMapping.url() && !"".equals(urlMapping.url())) {
					if (StrKit.notBlank(urlMapping.viewPath())) {
						routes.add(urlMapping.url(), (Class<? extends Controller>) clazz, urlMapping.viewPath());
					} else {
						routes.add(urlMapping.url(), (Class<? extends Controller>) clazz);
					}
				}
			}
		}
	}

	public void configPlugin(Plugins plugins) {
		plugins.add(new MessagePlugin());
		plugins.add(new EhCachePlugin());

		if (Jpress.isInstalled()) {
			DruidPlugin druidPlugin = createDruidPlugin();
			plugins.add(druidPlugin);

			ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(druidPlugin);
			plugins.add(activeRecordPlugin);
		}
	}

	public DruidPlugin createDruidPlugin() {

		Prop dbProp = PropKit.use("db.properties");
		String db_host = dbProp.get("db_host").trim();
		String db_name = dbProp.get("db_name").trim();
		String db_user = dbProp.get("db_user").trim();
		String db_password = dbProp.get("db_password").trim();

		return DbDialectFactory.getDbDialect().createDuidPlugin(db_host, db_name, db_user, db_password);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ActiveRecordPlugin createRecordPlugin(DruidPlugin druidPlugin) {
		ActiveRecordPlugin arPlugin = new ActiveRecordPlugin(druidPlugin);
		List<Class<Model>> modelClassList = ClassScaner.scanSubClass(Model.class);
		if (modelClassList != null) {
			String tablePrefix = PropKit.use("db.properties").get("db_tablePrefix");
			tablePrefix = (StrKit.isBlank(tablePrefix)) ? "" : (tablePrefix.trim());
			for (Class<?> clazz : modelClassList) {
				Table tb = clazz.getAnnotation(Table.class);
				if (tb == null)
					continue;
				String tname = tablePrefix + tb.tableName();
				if (null != tb.primaryKey() && !"".equals(tb.primaryKey())) {
					arPlugin.addMapping(tname, tb.primaryKey(), (Class<? extends Model<?>>) clazz);
				} else {
					arPlugin.addMapping(tname, (Class<? extends Model<?>>) clazz);
				}

				DbDialect.mapping(clazz.getSimpleName().toLowerCase(), tname);
			}
		}

		arPlugin.setShowSql(JFinal.me().getConstants().getDevMode());

		return arPlugin;
	}

	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new GlobalInterceptor());
		interceptors.add(new JI18nInterceptor());
		interceptors.add(new HookInterceptor());
		interceptors.add(new AdminInterceptor());
	}

	public void configHandler(Handlers handlers) {
		handlers.add(new JHandler());
		DruidStatViewHandler druidViewHandler = new DruidStatViewHandler("/admin/druid");
		handlers.add(druidViewHandler);
	}

	@Override
	public void afterJFinalStart() {
		if (Jpress.isInstalled()) {
			Jpress.loadFinished();
		}
		onJfinalStarted();
	}

	public abstract void onJfinalStarted();

}

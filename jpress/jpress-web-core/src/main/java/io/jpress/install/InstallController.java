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
package io.jpress.install;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/install", viewPath = "/WEB-INF/install")
@Before(InstallInterceptor.class)
public class InstallController extends JBaseController {
	
	private static final Log log = Log.getLog(InstallController.class);

	public void index() {
		render("step1.html");
	}

	public void step2() {
		String db_host = getPara("db_host");
		String db_host_port = getPara("db_host_port");
		db_host_port = StringUtils.isNotBlank(db_host_port) ? db_host_port.trim() : "3306";
		String db_name = getPara("db_name");
		String db_user = getPara("db_user");
		String db_password = getPara("db_password");
		String db_table_prefix = getPara("db_tablePrefix");

		if (!StrKit.notBlank(db_host,db_host_port, db_name, db_user)) {
			render("step2.html");
			return;
		}

		InstallUtils.init(db_host,db_host_port,db_name, db_user, db_password, db_table_prefix);

		try {
			List<String> tableList = InstallUtils.getTableList();
			if (null != tableList && tableList.size() > 0) {
				if (tableList.contains(db_table_prefix + "attachment")
						|| tableList.contains(db_table_prefix + "comment")
						|| tableList.contains(db_table_prefix + "content")
						|| tableList.contains(db_table_prefix + "mapping")
						|| tableList.contains(db_table_prefix + "metadata")
						|| tableList.contains(db_table_prefix + "option")
						|| tableList.contains(db_table_prefix + "taxonomy")
						|| tableList.contains(db_table_prefix + "user")) { // table
																			// has
																			// contains
					redirect("/install/step2_error");
					return;
				}
			}
		} catch (Exception e) { // db config error
			e.printStackTrace();
			redirect("/install/step2_error");
			return;
		}

		try {

			InstallUtils.createJpressDatabase();

			List<String> tableList = InstallUtils.getTableList();
			if (null != tableList && tableList.size() > 0) {
				if (tableList.contains(db_table_prefix + "attachment")
						&& tableList.contains(db_table_prefix + "comment")
						&& tableList.contains(db_table_prefix + "content")
						&& tableList.contains(db_table_prefix + "mapping")
						&& tableList.contains(db_table_prefix + "metadata")
						&& tableList.contains(db_table_prefix + "option")
						&& tableList.contains(db_table_prefix + "taxonomy")
						&& tableList.contains(db_table_prefix + "user")) {
					// createJpressDatabase success
					redirect("/install/step3");
					return;
				}
			}

		} catch (Exception e) {
			log.error("InstallController step2 is erro", e);
		}

		redirect("/install/step2_error");
	}

	public void step2_error() {

		render("step2_error.html");
	}

	public void step3() throws SQLException {

		String webname = getPara("webname");
		String username = getPara("username");
		String password = getPara("password");

		if (StrKit.isBlank(webname) || StrKit.isBlank(username) || StrKit.isBlank(password)) {
			keepPara();
			render("step3.html");
			return;
		}

		InstallUtils.setWebName(webname);

		String salt = EncryptUtils.salt();
		password = EncryptUtils.encryptPassword(password, salt);
		InstallUtils.setWebFirstUser(username, password, salt);

		InstallUtils.createDbProperties();
		InstallUtils.createJpressProperties();

		redirect("/");
	}

}

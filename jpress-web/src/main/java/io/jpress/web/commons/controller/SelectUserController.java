/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.commons.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/commons/select/user", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class SelectUserController extends AdminControllerBase {


    @Inject
    private UserService userService;


    public void index() {
        Columns columns = Columns.create().likeAppendPercent("username",get("username"))
                .likeAppendPercent("nickname",get("nickname"))
                .likeAppendPercent("mobile",get("mobile"));

        Page<User> page = userService._paginate(getPagePara(), getPageSizePara(), columns, null);
        setAttr("page", page);
        render("user/commons_select_user.html");
    }


}

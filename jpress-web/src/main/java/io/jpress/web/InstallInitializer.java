/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web;

import io.jboot.Jboot;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.front.InstallController;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于在应用启动的时候，检测 JPress 是否已经安装
 * @Package io.jpress.web
 */
public class InstallInitializer {

    private static InstallInitializer me = new InstallInitializer();

    private InstallInitializer() {

    }

    public static InstallInitializer me() {
        return me;
    }

    public void init() {

        UserService service = Jboot.bean(UserService.class);

        if (service.findCountByStatus(User.STATUS_OK) > 0) {
            InstallController.setInstalled(true);
        } else {
            InstallController.setInstalled(false);
        }

    }


}

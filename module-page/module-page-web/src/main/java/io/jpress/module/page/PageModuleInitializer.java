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
package io.jpress.module.page;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import io.jboot.db.model.Columns;
import io.jboot.utils.DateUtil;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleBase;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 应用启动监听器
 * @Package io.jpress.module.page
 */
public class PageModuleInitializer extends ModuleBase {


    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("page");
        menuGroup.setText("页面");
        menuGroup.setIcon("<i class=\"fas fa-file\"></i>");
        menuGroup.setOrder(2);

        adminMenus.add(menuGroup);

    }

    @Override
    public String onRenderDashboardBox(Controller controller) {

        Integer date = controller.getParaToInt("date");

        Columns columns = new Columns();
        columns.eq("status",SinglePage.STATUS_NORMAL);

        //如果是今天
        if(date !=null && date == 0){

            columns.between("created", DateUtil.getStartOfDay(new Date()), DateUtil.getEndOfDay(new Date()));
        }

        //最多就让查 28 天
        else if (date != null && date > 0 && date < 29) {

            //创建日历类对象
            Calendar calendar = Calendar.getInstance();

            //设置当前时间
            calendar.setTime(new Date());

            //设置当前时间 加 几天
            calendar.add(Calendar.DATE, -date);

            columns.between("created", DateUtil.getStartOfDay(calendar.getTime()), DateUtil.getStartOfDay(new Date()));

        }

        List<SinglePage> singlePages = Aop.get(SinglePageService.class).findListByColumns(columns, "created desc", 5);
        controller.setAttr("singlePages",singlePages);


        return "page/_dashboard_box.html";
    }

}

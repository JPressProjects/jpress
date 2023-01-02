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
package io.jpress.module.form;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.db.model.Columns;
import io.jboot.utils.DateUtil;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @Title: Module 监听器
 * @Description: 每个 module 都应该有这样的一个监听器，用来配置自身Module的信息，比如后台菜单等
 * @Package io.jpress.module.form
 */
public class FormModuleInitializer extends JbootAppListenerBase implements ModuleListener {


    @Override
    public String onRenderDashboardBox(Controller controller) {

        Integer date = controller.getParaToInt("date");

        Columns columns = new Columns();
        columns.eq("status", FormInfo.FORMINFO_STATUS_PUBLISHED);

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

        List<FormInfo> formInfoList = Aop.get(FormInfoService.class).findListByColumns(columns, "id desc", 5);
        controller.setAttr("formInfoList",formInfoList);

        return "form/_dashboard_box.html";
    }

    @Override
    public String onRenderToolsBox(Controller controller) {
        return null;
    }

    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {
		MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("form");
        menuGroup.setText("表单");
        menuGroup.setIcon("<i class=\"fa fa-table\"></i>");
        menuGroup.setOrder(102);
        adminMenus.add(menuGroup);
    }



}

/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.form.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Map;


@RequestMapping(value = "/admin/form/data", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormDataController extends AdminControllerBase {

    @Inject
    private FormInfoService formInfoService;

    @Inject
    private FormDataService formDataService;


    /**
     * 数据列表
     */
    public void index() {
        FormInfo formInfo = formInfoService.findById(getParaToLong());
        setAttr("form", formInfo);

        Columns columns = Columns.create();


        Map<String, String> paras = getParas();
        if (paras != null) {
            paras.forEach((s, s2) -> {
                if (formInfo.isField(s)) {
                    columns.likeAppendPercent(s, s2);
                }
            });
        }


        Page<Record> page = formDataService.paginateByColumns(formInfo.getDataTableName(), getPagePara(), getPageSizePara(), columns);
        setAttr("page", page);


        render("form/form_data_list.html");
    }


    /**
     * 数据详情
     */
    public void detail() {

        Long formId = getParaToLong("formId");
        if (formId == null) {
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findById(formId);
        if (formInfo == null) {
            renderError(404);
            return;
        }
        setAttr("form", formInfo);


        Record record = formDataService.findById(formInfo.getDataTableName(), getParaToLong("dataId"));
        setAttr("record", record);


        render("form/form_data_detail.html");
    }


}

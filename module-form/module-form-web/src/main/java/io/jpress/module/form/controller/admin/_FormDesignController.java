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
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;


@RequestMapping(value = "/admin/form/design", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormDesignController extends AdminControllerBase {

    @Inject
    private FormInfoService service;

    public void index() {
        FormInfo formInfo = service.findById(getParaToLong());
        setAttr("form",formInfo);
        render("form/form_design.html");
    }


    public void save(){
        FormInfo formInfo = service.findById(getParaToLong("id"));

        if(formInfo.getStatus() != null && formInfo.getStatus().equals(FormInfo.FORMINFO_STATUS_PUBLISHED)){
            renderFailJson("表单已发布 更新失败");
            return;
        }

        formInfo.setBuilderJson(getRawData());
        service.update(formInfo);

        renderOkJson();
    }


}

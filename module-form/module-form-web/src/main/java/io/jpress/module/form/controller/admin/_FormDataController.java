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
package io.jpress.module.form.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.liaochong.myexcel.core.EnjoyExcelBuilder;
import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.module.form.model.EChartsItem;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


        Page<Record> page = formDataService.paginateByColumns(formInfo.getCurrentTableName(), getPagePara(), getPageSizePara(), columns);
        setAttr("page", page);


        render("form/form_data_list.html");
    }


    /**
     * 删除数据
     */
    public void doDel() {
        FormInfo formInfo = formInfoService.findById(getParaToLong());
        setAttr("form", formInfo);

        if (formInfo == null) {
            renderFailJson();
            return;
        }

        formDataService.deleteById(formInfo.getCurrentTableName(), getParaToLong("id"));
        renderOkJson();
    }


    /**
     * 批量删除数据
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {

        FormInfo formInfo = formInfoService.findById(getParaToLong());

        Object[] ids = getParaSet("ids").toArray();

        if (formInfo == null || ids.length <= 0) {
            renderFailJson();
            return;
        }

        for (Object id : ids) {
            formDataService.deleteById(formInfo.getCurrentTableName(), Long.parseLong(id.toString()));
        }

        renderOkJson();
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


        Record record = formDataService.findById(formInfo.getCurrentTableName(), getParaToLong("dataId"));
        setAttr("record", record);


        render("form/form_data_detail.html");
    }


    /**
     * 导出为excel
     */
    public void excelExport() {

        try (EnjoyExcelBuilder excelBuilder = new EnjoyExcelBuilder()) {

            FormInfo formInfo = formInfoService.findById(getParaToLong());

            List<Record> records = formDataService.findAll(formInfo.getCurrentTableName());
            Map<String, Object> datas = new HashMap<>();
            datas.put("records", records);
            datas.put("form", formInfo);

            excelBuilder.fileTemplate(PathKit.getWebRootPath() + "/WEB-INF/views/admin/form/", "form_data_excel.html");

            Workbook workbook = excelBuilder.build(datas);
            AttachmentExportUtil.export(workbook, formInfo.getName(), getResponse());
            renderNull();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 图表 echars 页面
     */
    public void formCharts() {

        setAttr("formId",getParaToLong());

        setAttr("field",getPara("field"));

        render("form/form_data_charts.html");
    }

    /**
     * 图表 echars 数据
     */
    public void formChartsData(){

        FormInfo formInfo = formInfoService.findById(getParaToLong());

        JSONArray datas = JSONArray.parseArray(formInfo.getBuilderJson());
        JSONArray options = getOptions(datas, getPara("field"));

        List<EChartsItem> chartsInfoList = new ArrayList<>();

        if(options != null && options.size() > 0){

            for (int i = 0; i < options.size(); i++) {

                    EChartsItem formChartsInfo = new EChartsItem();

                    JSONObject jsonObject = options.getJSONObject(i);

                    Long count = formDataService.findCountByValue(formInfo.getCurrentTableName(), getPara("field"), jsonObject.getString("value"));

                    formChartsInfo.setName(jsonObject.getString("text") + "(" + count +")");

                    formChartsInfo.setValue(count);

                    chartsInfoList.add(formChartsInfo);

            }

        }

        renderJson(chartsInfoList);
    }



    private JSONArray getOptions(JSONArray datas,String field){
        if (datas == null || datas.size()== 0){
            return null;
        }

        for (int i = 0; i < datas.size(); i++) {

            JSONObject jsonObject = datas.getJSONObject(i);
            JSONArray options = jsonObject.getJSONArray("options");

            if (options != null && field.equals(jsonObject.getString("field"))){
                return options;
            }

            JSONObject children = jsonObject.getJSONObject("children");

            if (children != null) {
                for (String key : children.keySet()) {
                    JSONArray jsonArray = children.getJSONArray(key);
                    JSONArray childOptions = getOptions(jsonArray, field);
                    if (childOptions != null){
                        return childOptions;
                    }
                }
            }


        }

        return null;
    }

}

package io.jpress.module.job.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.commons.utils.UrlUtils;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.List;

/**
 * @description: 分页查询 job
 * @version: v5.0
 **/
@JFinalDirective("jobPage")
public class JobPageDirective extends JbootDirectiveBase {

    @Inject
    private JobService jobService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        TemplateControllerBase controller = (TemplateControllerBase) JbootControllerContext.get();

        int page = controller.getPageNumber();

        int pageSize = getParaToInt("pageSize", scope, 10);

        String orderBy = getPara("orderBy", scope, "created desc");

        //可以指定学历
        Integer education = getParaToInt("education", scope);
        //可以指定年限
        Integer workYear = getParaToInt("workYear", scope);
        //可以指定工作类型
        Integer workType = getParaToInt("workType", scope);
        //可以指定招聘类型
        Integer recruitmentType = getParaToInt("recruitmentType", scope);

        //可以指定分类ID
        Long categoryId = getParaToLong("categoryId", scope);
        //获取当前页面 key 为 category 的数据
        JobCategory jobCategory = controller.getAttr("category");
        //如果 categoryId 为 0 并且 jobCategory不为空的话 使用 jobCategory 的 id
        if (categoryId == null && jobCategory != null) {
            categoryId = jobCategory.getId();
        }

        //同理 可以指定部门ID
        Long deptId = getParaToLong("deptId", scope);
        JobDepartment jobDept = controller.getAttr("dept");
        if (deptId == null && jobDept != null) {
            deptId = jobDept.getId();
        }

        //同理 可以指定部门ID
        Long addressId = getParaToLong("addressId", scope);
        JobAddress jobAddress = controller.getAttr("address");
        if (addressId == null && jobAddress != null) {
            addressId = jobAddress.getId();
        }

        Columns columns = new Columns();
        columns.eq("category_id", categoryId);
        columns.eq("dept_id", deptId);
        columns.eq("address_id", addressId);
        columns.eq("education", education);
        columns.eq("work_year", workYear);
        columns.eq("recruitment_type", recruitmentType);
        columns.eq("work_type", workType);

        //分页查询并添加信息
        Page<Job> jobPage = jobService.paginateByColumnsWithInfo(page, pageSize, columns, orderBy);

        //如果查询不到数据  直接 return
        if (jobPage.getList().isEmpty()) {
            return;
        }

        scope.setGlobal("jobPage", jobPage);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("jobPaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber, Env env, Scope scope, Writer writer) {

            List<Column> jobColumnsList = JbootControllerContext.get().getAttr("jobColumnsList");

            if (pageNumber >= 1 && jobColumnsList != null) {

                StringBuilder myUrl = new StringBuilder();

                for (int i = 0; i < jobColumnsList.size(); i++) {
                    if (i == 0) {
                        myUrl.append("?").append(jobColumnsList.get(i).getName()).append("=").append(jobColumnsList.get(i).getValue());
                    } else {
                        myUrl.append("&").append(jobColumnsList.get(i).getName()).append("=").append(jobColumnsList.get(i).getValue());
                    }
                }

                return  UrlUtils.getUrl("/job/list", "" , myUrl.toString() + "&page=" + pageNumber);
            }else {
                return UrlUtils.getUrl("/job/list", "?", "page=" + (pageNumber > 0 ? pageNumber : 1));
            }
          //TODO
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("jobPage");
        }

    }

}

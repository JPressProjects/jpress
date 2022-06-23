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
package io.jpress.module.job.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobAddressService;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.service.JobDepartmentService;
import io.jpress.module.job.service.JobService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;
import java.util.List;


@RequestMapping(value = "/admin/job", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _JobController extends AdminControllerBase {

    @Inject
    private JobService service;

    @Inject
    private JobCategoryService jobCategoryService;

    @Inject
    private JobDepartmentService jobDepartmentService;

    @Inject
    private JobAddressService jobAddressService;

    @Inject
    private MenuService menuService;

    @AdminMenu(text = "岗位管理", groupId = "job", order = 0)
    public void index() {
        Page<Job> entries = service.paginate(getPagePara(), getPageSizePara());
        setAttr("page", entries);
        render("job/job_list.html");
    }

    public void edit() {
        int entryId = getParaToInt(0, 0);

        Job entry = entryId > 0 ? service.findById(entryId) : null;
        setAttr("job", entry);
        set("now", new Date());
        render("job/job_edit.html");
    }

    public void doSave() {
        Job entry = getModel(Job.class, "job");
        Long id = (long) service.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", id));
    }

    public void doDel() {
        Long id = getIdPara();
        render(service.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        service.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    @AdminMenu(text = "分类管理", groupId = "job", order = 1)
    public void JobCategory() {

        Page<JobCategory> entries = jobCategoryService.paginate(getPagePara(), getPageSizePara());
        setAttr("page", entries);

        List<JobCategory> categoryList = jobCategoryService.findAll();
        setAttr("categoryList", categoryList);

        long entryId = getParaToLong(0, 0L);

        if (entryId > 0 && entries != null) {
            setAttr("jobCategory", jobCategoryService.findById(entryId));
            set("now", new Date());
        }


        render("job/job_category_list.html");

    }

    public void categoryDoSave() {
        JobCategory entry = getModel(JobCategory.class, "jobCategory");

        if (entry.getId() == null) {
            entry.setUserId(getLoginedUser().getId());
        }

        long id = (long) jobCategoryService.saveOrUpdate(entry);

        renderJson(Ret.ok().set("id", id));
    }

    public void categoryDoDel() {
        Long id = getIdPara();
        render(jobCategoryService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void categoryDoDelByIds() {
        jobCategoryService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    @AdminMenu(text = "部门管理", groupId = "job", order = 2)
    public void JobDept() {

        Page<JobDepartment> entries = jobDepartmentService.paginate(getPagePara(), getPageSizePara());
        setAttr("page", entries);


        long entryId = getParaToLong(0, 0L);

        if (entryId > 0 && entries != null) {
            setAttr("jobDepartment", jobDepartmentService.findById(entryId));
            set("now", new Date());
        }


        render("job/job_department_list.html");
    }

    public void deptDoSave() {
        JobDepartment entry = getModel(JobDepartment.class, "jobDepartment");
        long id = (long) jobDepartmentService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", id));
    }

    public void deptDoDel() {
        Long id = getIdPara();
        render(jobDepartmentService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void deptDoDelByIds() {
        jobDepartmentService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    @AdminMenu(text = "地址管理", groupId = "job", order = 3)
    public void JobAddress() {

        Page<JobAddress> entries = jobAddressService.paginate(getPagePara(), getPageSizePara());
        setAttr("page", entries);

        long entryId = getParaToLong(0, 0L);

        if (entryId > 0 && entries != null) {
            setAttr("jobAddress", jobAddressService.findById(entryId));
        }


        render("job/job_address_list.html");

    }

    public void addressDoSave() {
        JobAddress entry = getModel(JobAddress.class, "jobAddress");
        long id = (long) jobAddressService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", id));
    }

    public void addressDoDel() {
        Long id = getIdPara();
        render(jobAddressService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void addressDoDelByIds() {
        jobAddressService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


}

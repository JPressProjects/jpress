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
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobApply;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobApplyService;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.service.JobService;
import io.jpress.service.MenuService;
import io.jpress.service.OptionService;
import io.jpress.web.base.AdminControllerBase;
import java.util.*;


@RequestMapping(value = "/admin/job", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _JobController extends AdminControllerBase {

    @Inject
    private JobService service;

    @Inject
    private JobCategoryService jobCategoryService;


    @Inject
    private JobApplyService jobApplyService;

    @Inject
    private MenuService menuService;

    @Inject
    private OptionService optionService;

    @AdminMenu(text = "岗位管理", groupId = "job", order = 0)
    public void list() {
        Columns columns = new Columns();
        Page<Job> entries = service.paginateByColumnsWithInfo(getPagePara(), getPageSizePara(), columns, "created desc");
        setAttr("page", entries);


        render("job/job_list.html");
    }

    public void add() {

        List<JobCategory> categoryList = jobCategoryService.findListByColumns(Columns.create().eq("type",JobCategory.CATEGORY_TYPE_CATEGORY));
        setAttr("categoryList", categoryList);

        List<JobCategory> addressList = jobCategoryService.findListByColumns(Columns.create().eq("type",JobCategory.CATEGORY_TYPE_ADDRESS));
        setAttr("addressList", addressList);

        render("job/job_edit.html");
    }

    public void edit() {
        int entryId = getParaToInt(0, 0);

        List<JobCategory> categoryList = jobCategoryService.findListByColumns(Columns.create().eq("type",JobCategory.CATEGORY_TYPE_CATEGORY));
        setAttr("categoryList", categoryList);

        List<JobCategory> addressList = jobCategoryService.findListByColumns(Columns.create().eq("type",JobCategory.CATEGORY_TYPE_ADDRESS));
        setAttr("addressList", addressList);

        Job entry = entryId > 0 ? service.findById(entryId) : null;
        setAttr("job", entry);
        set("now", new Date());
        render("job/job_edit.html");
    }

    public void doSave() {
        Job entry = getModel(Job.class, "job");

        if (entry.getTitle() == null) {
            renderFailJson("名称不能为空");
            return;
        }

        if (entry.getId() == null) {
            entry.setCreated(new Date());
        }

        if (entry.getWithNotify() == null) {
            entry.setWithNotify(false);
        }

        if (entry.getWithRemote() == null) {
            entry.setWithRemote(false);
        }

        if (entry.getWithApply() == null) {
            entry.setWithApply(false);
        }


        if (entry.getExpiredTo() != null && entry.getExpiredTo().before(entry.getCreated())) {
            renderFailJson("请正确填写岗位有效时间");
            return;
        }

        //开始年龄 和 结束年龄
        // 如果结束年龄  < 开始年龄 不行
        // 如果俩个你年龄等于 0 不行
        if ((entry.getAgeLimitStart() != null && entry.getAgeLimitEnd() != null) &&
                (entry.getAgeLimitEnd() < entry.getAgeLimitStart() ||
                        (entry.getAgeLimitStart() <= 0 || entry.getAgeLimitEnd() <= 0))) {
            renderFailJson("请正确填写年龄要求");
            return;
        }

        //存放 需要更新count的 category 的id
        List<Long> categoryIds = new ArrayList<>();
        //存放 需要更新count的 dept 的id
        List<Long> deptIds = new ArrayList<>();


        //更新 分类下的岗位数量
        //如果是新建 那么只更新一个
        if (entry.getId() == null && entry.getCategoryId() != null) {
            categoryIds.add(entry.getCategoryId());
        }
        //如果不是新建 那么更新俩个
        else if (entry.getId() != null && entry.getCategoryId() != null) {
            Job job = service.findById(entry.getId());
            if (job != null && !job.getCategoryId().equals(entry.getCategoryId())) {
                categoryIds.add(entry.getCategoryId());
                categoryIds.add(job.getCategoryId());
            }
        }

        Long id = (long) service.saveOrUpdate(entry);

        jobCategoryService.updateCount(categoryIds);

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

        Columns columns = new Columns();
        columns.eq("type", JobCategory.CATEGORY_TYPE_CATEGORY);
        Page<JobCategory> entries = jobCategoryService.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page", entries);
        setAttr("type", JobCategory.CATEGORY_TYPE_CATEGORY);

        List<JobCategory> categoryList = jobCategoryService.findListByColumns(columns);
        setAttr("categoryList", categoryList);

        long entryId = getParaToLong(0, 0L);

        if (entryId > 0 && entries != null) {
            setAttr("jobCategory", jobCategoryService.findById(entryId));
        }

        render("job/job_category_list.html");

    }

    public void categoryDoSave() {
        JobCategory entry = getModel(JobCategory.class, "jobCategory");

        if (entry.getId() == null) {
            entry.setType(JobCategory.CATEGORY_TYPE_CATEGORY);
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

    //地址管理 和 分类管理 共用一张表 区别在于 字段 type
    @AdminMenu(text = "地址管理", groupId = "job", order = 2)
    public void JobAddress() {

        Columns columns = new Columns();
        columns.eq("type", JobCategory.CATEGORY_TYPE_ADDRESS);
        Page<JobCategory> entries = jobCategoryService.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page", entries);

        setAttr("type", JobCategory.CATEGORY_TYPE_ADDRESS);

        List<JobCategory> categoryList = jobCategoryService.findListByColumns(columns);
        setAttr("addressList", categoryList);

        long entryId = getParaToLong(0, 0L);

        if (entryId > 0 && entries != null) {
            setAttr("jobCategory", jobCategoryService.findById(entryId));
        }


        render("job/job_category_list.html");

    }

    public void addressDoSave() {
        JobCategory entry = getModel(JobCategory.class, "jobCategory");

        if (entry.getId() == null) {

            entry.setType(JobCategory.CATEGORY_TYPE_ADDRESS);

        }

        long id = (long) jobCategoryService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", id));
    }

    @AdminMenu(text = "简历管理", groupId = "job", order = 3)
    public void JobApply() {

        Columns columns = new Columns();
        Page<JobApply> page = jobApplyService.paginateByColumnsWithInfo(getPagePara(), getPageSizePara(), columns, "created desc");
        setAttr("page", page);

        render("job/job_apply_list.html");
    }

    @ActionKey("./JobApply/detail")
    public void applyDetail() {

        Long id = getLong();

        JobApply jobApply = jobApplyService.findById(id);

        if (id == null || jobApply == null) {
            renderError(404);
            return;
        }

        //更新查看状态
        jobApply.setWithViewed(true);
        jobApply.update();

        setAttr("jobApply", jobApply);

        Job job = service.findById(jobApply.getJobId());

        if (job != null) {
            setAttr("job", job);
        }

        render("job/job_apply_detail.html");
    }

    public void applyResult() {

        Long id = getLong();


        JobApply jobApply = jobApplyService.findById(id);

        if (id == null || jobApply == null) {
            renderError(404);
            return;
        }

        String content = getPara("disposedContent");
        if (content == null) {
            renderFailJson("请填写处理意见");
            return;
        }

        jobApply.setDisposedContent(content);
        jobApply.setDisposedTime(new Date());
        jobApply.setWithDisposed(true);

        jobApply.update();

        renderOkJson();
    }

    public void applyDoDel() {
        Long id = getIdPara();
        render(jobApplyService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void applyDoDelByIds() {
        jobApplyService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    @AdminMenu(text = "招聘设置", groupId = "job", order = 4)
    public void setting() {
        render("job/job_setting.html");
    }

}

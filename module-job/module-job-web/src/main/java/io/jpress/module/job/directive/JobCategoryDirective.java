package io.jpress.module.job.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version V5.0
 * @description: 所有岗位分类
 */
@JFinalDirective("jobCategories")
public class JobCategoryDirective extends JbootDirectiveBase {


    @Inject
    private JobCategoryService jobCategoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        boolean asTree = getParaToBool("asTree", scope, Boolean.FALSE);
        Long pId = getParaToLong("parentId", scope);

        List<JobCategory> categoryList = jobCategoryService.findListByColumns(Columns.create().eq("type",JobCategory.CATEGORY_TYPE_CATEGORY));

        SortKit.toLayer(categoryList);

        SortKit.fillParentAndChild(categoryList);

        //根据pid 查询分类
        if(pId !=null && pId > 0){
            categoryList = categoryList.stream().filter(category -> {
                JobCategory parent = (JobCategory) category.getParent();
                return parent != null && pId.equals(parent.getId());
            }).collect(Collectors.toList());
        }

        //返回树状结构
        if (asTree) {
            SortKit.toTree(categoryList);
        }

        if (categoryList == null) {
            return;
        }

        scope.setLocal("categoryList", categoryList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

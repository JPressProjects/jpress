package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;


/**
 * @program: _AttachmentFormController
 * @description:
 * @create: 2022/7/26 13:53
 **/
@RequestMapping(value = "/admin/attachment/form", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AttachmentFormController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentFormController.class);

    @Inject
    private FormInfoService formInfoService;

    /**
     * 选择表单
     */
    public void browse() {

        Columns columns = new Columns();
        Page<FormInfo> page = formInfoService.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page",page);

        render("attachment/form_browse.html");
    }

    //TODO



}

package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.AttachmentVideo;
import io.jpress.model.AttachmentVideoCategory;
import io.jpress.service.AttachmentVideoCategoryService;
import io.jpress.service.AttachmentVideoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;

@RequestMapping(value = "/admin/attachment/video", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AttachmentVideoController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentVideoController.class);


    @Inject
    private AttachmentVideoService attachmentVideoService;

    @Inject
    private AttachmentVideoCategoryService videoCategoryService;

    @AdminMenu(text = "视频", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 29)
    public void list() {
        Columns columns = Columns.create();
        //条件查询
        columns.likeAppendPercent("vod_name",getPara("title"));
        columns.eq("category_id",getPara("categoryId"));
        Page<AttachmentVideo> page = attachmentVideoService.paginateByColumns(getPagePara(), getPageSizePara(), columns, "id desc");
        if(page != null){
            for (AttachmentVideo attachmentVideo : page.getList()) {
                if(attachmentVideo.getCategoryId() != null){
                    AttachmentVideoCategory category = videoCategoryService.findById(attachmentVideo.getCategoryId());
                    attachmentVideo.put("category",category);
                }
            }
        }
        setAttr("page",page);

        //视频分类
        List<AttachmentVideoCategory> categories = videoCategoryService.findAll();
        setAttr("categories",categories);

        render("attachment/video_list.html");
    }


    public void edit(){
        String id = getPara();
        AttachmentVideo video = attachmentVideoService.findById(id);
        setAttr("video",video);

        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories",categories);

        render("attachment/video_add.html");
    }

    public void add(){
        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories",categories);

        render("attachment/video_add.html");
    }

    public void doSave(){
        AttachmentVideo video = getModel(AttachmentVideo.class, "video");

        attachmentVideoService.saveOrUpdate(video);
        //更新视频分类下的内容数量
        videoCategoryService.doUpdateVideoCategoryCount(video.getCategoryId());
        renderOkJson();
    }

    @AdminMenu(text = "视频分类", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 39)
    public void category(){
        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories",categories);
        long id = getParaToLong(0, 0L);
        if (id > 0 && categories != null) {
            for (AttachmentVideoCategory category : categories) {
                if (category.getId().equals(id)) {
                    setAttr("category", category);
                }
            }
        }

        render("attachment/video_category_list.html");

    }

    public void doCategorySave() {
        AttachmentVideoCategory category = getModel(AttachmentVideoCategory.class, "category");

        videoCategoryService.saveOrUpdate(category);
        //更新视频分类下的内容数量
        videoCategoryService.doUpdateVideoCategoryCount(category.getId().intValue());
        renderOkJson();
    }



    public void doCategoryDel() {
        videoCategoryService.deleteById(getIdPara());
        renderOkJson();
    }

    public void doDel(){
        attachmentVideoService.deleteById(getIdPara());
        renderOkJson();
    }


}

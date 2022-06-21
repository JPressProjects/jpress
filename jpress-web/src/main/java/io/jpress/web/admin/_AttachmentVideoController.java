package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.aliyun.AliyunVideoUtil;
import io.jpress.commons.aliyun.CloudVideoInfo;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.AttachmentVideo;
import io.jpress.model.AttachmentVideoCategory;
import io.jpress.service.AttachmentVideoCategoryService;
import io.jpress.service.AttachmentVideoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Map;

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
        columns.eq("video_type",getPara("type"));
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
        if(video.getVodVid() != null){
            String playauth = AliyunVideoUtil.getPlayAuth(video.getVodVid());
            setAttr("cloudPlayAuth", playauth);
            setAttr("cloudVid", video.getVodVid());
        }

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

    public void doGetVideoInfo(String videoId){

        //视频点播
        CloudVideoInfo videoInfo = AliyunVideoUtil.getVideoInfo(videoId);
        renderJson(Ret.ok()
                .set("videoId", videoInfo.getVideoId())
                .set("size", videoInfo.getSize()));
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


    /**
     * 获取视频上传凭证
     *
     * @param fileName
     * @param title
     * @return
     */
    public Ret doGetUploadVideoAuth(String fileName, String title) {
        Map<String, Object> authMap = AliyunVideoUtil.getUploadVideoAuth(fileName, title);
        return authMap != null ? Ret.ok().set(authMap) : Ret.fail();
    }


    /**
     * 刷新视频上传凭证
     *
     * @param videoId
     * @return
     */
    public Ret doRefreshVideoAuth(String videoId) {
        Map<String, Object> authMap = AliyunVideoUtil.refreshUploadVideoAuth(videoId);
        return authMap != null ? Ret.ok().set(authMap) : Ret.fail();
    }

    /**
     * 获取播放凭证
     *
     * @param videoId
     * @return
     */
    public Ret doGetPlayAuth(String videoId) {
        return Ret.ok().set("playAuth", AliyunVideoUtil.getPlayAuth(videoId));
    }



}

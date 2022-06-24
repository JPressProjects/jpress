package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.aliyun.AliyunLiveUtil;
import io.jpress.commons.aliyun.AliyunVideoUtil;
import io.jpress.commons.aliyun.CloudVideoInfo;
import io.jpress.commons.txunyun.Signature;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.AttachmentVideo;
import io.jpress.model.AttachmentVideoCategory;
import io.jpress.service.AttachmentVideoCategoryService;
import io.jpress.service.AttachmentVideoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
        String playauth = AliyunVideoUtil.getPlayAuth(video.getVodVid());
        //点播视频
        setAttr("cloudPlayAuth", playauth);
        setAttr("cloudVid", video.getVodVid());

        //直播, m3u8 是延迟最高（延迟在 40s 左右）的，但是浏览器的兼容性是最好的
        setAttr("livePlayUrl", AliyunLiveUtil.createPlayUrlForM3U8(video.getLiveStream()));
        //直播回放
        setAttr("liveCloudPlayAuth", playauth);
        setAttr("liveCloudVid", video.getVodVid());

        setAttr("video",video);

        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories",categories);

        //视频云类型
        String cloudType = JPressOptions.get("attachment_cloud_type");
        setAttr("cloudType",cloudType);

        render("attachment/video_add.html");
    }

    public void add(){
        //视频云类型
        String cloudType = JPressOptions.get("attachment_cloud_type");
        setAttr("cloudType",cloudType);

        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories",categories);

        render("attachment/video_add.html");
    }

    @EmptyValidate({
            @Form(name = "video.vod_name", message = "视频标题不能为空")
    })
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


//    @AdminMenu(text = "视频分类", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 39)
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
        if(authMap == null){
            renderFailJson("请先配置点播视频的密钥!");
        }
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
        if(authMap == null){
            renderFailJson("请先配置点播视频的密钥!");
        }
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


    /**
     * 创建直播流
     *
     * @return
     */
    public Ret doGetLiveInfo() {
        String streamName = StrUtil.uuid();

        String appName = AliyunLiveUtil.getAppName();
        String playDomain = AliyunLiveUtil.getPlayDomain();

        String pushUrl = AliyunLiveUtil.createPushUrl(streamName);
        String playUrl = AliyunLiveUtil.createPlayUrlForM3U8(streamName);

        return Ret.ok().set("pushUrl", pushUrl).set("playUrl", playUrl).set("liveApp", appName)
                        .set("domainName", playDomain).set("streamName", streamName);
    }


    /**
     * 获取签名
     * @return
     */
    public String getSign() {
        //得到Sign
        Signature sign = new Signature();
        //个人API密钥中的Secret Id
        sign.setSecretId("AKIDxiFVWIqDJ8gyg7G8jx6zd17vTlmX1EAo");
        //个人API密钥中的Secret Key
        sign.setSecretKey("7UcUGvPedMwHYgZDcpnVzDBtNccNuBE3");
        sign.setCurrentTime(System.currentTimeMillis() / 1000);
        sign.setRandom(new Random().nextInt(java.lang.Integer.MAX_VALUE));
        sign.setSignValidDuration(3600 * 24 * 2);

        String signature = null;
        try {
            signature = sign.getUploadSignature();
            System.out.println("signature : " + signature);
        } catch (Exception e) {
            System.out.print("获取签名失败");
            e.printStackTrace();
        }

        return signature;
    }


}

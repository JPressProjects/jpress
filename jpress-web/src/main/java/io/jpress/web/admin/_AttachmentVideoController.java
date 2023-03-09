package io.jpress.web.admin;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.db.model.Columns;
import io.jboot.utils.JsonUtil;
import io.jboot.utils.StrUtil;
import io.jboot.utils.TypeDef;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.aliyun.AliyunLiveUtil;
import io.jpress.commons.aliyun.AliyunVideoUtil;
import io.jpress.commons.aliyun.CloudVideoInfo;
import io.jpress.commons.qcloud.QCloudLiveUtil;
import io.jpress.commons.qcloud.QCloudVideoUtil;
import io.jpress.commons.qcloud.Signature;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.AttachmentVideo;
import io.jpress.model.AttachmentVideoCategory;
import io.jpress.service.AttachmentVideoCategoryService;
import io.jpress.service.AttachmentVideoService;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;
import java.util.*;

@RequestMapping(value = "/admin/attachment/video", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AttachmentVideoController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentVideoController.class);


    @Inject
    private AttachmentVideoService attachmentVideoService;

    @Inject
    private AttachmentVideoCategoryService videoCategoryService;

    @AdminMenu(text = "视频", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 29)
    public void list() {

        //设置分页数量
        setPaginateSizeSpacing(8);

        Columns columns = Columns.create();
        //条件查询
        columns.likeAppendPercent("vod_name", getPara("title"));
        columns.eq("video_type", getPara("type"));
        columns.eq("category_id", getPara("categoryId"));
        Page<AttachmentVideo> page = attachmentVideoService.paginateByColumns(getPagePara(), getPageSizePara(), columns, "id desc");
        if (page != null) {
            for (AttachmentVideo attachmentVideo : page.getList()) {
                if (attachmentVideo.getCategoryId() != null) {
                    AttachmentVideoCategory category = videoCategoryService.findById(attachmentVideo.getCategoryId());
                    attachmentVideo.put("category", category);
                }
            }
        }
        setAttr("page", page);

        //视频分类
        List<AttachmentVideoCategory> categories = videoCategoryService.findAll();
        setAttr("categories", categories);

        render("attachment/video_list.html");
    }


    public void edit() {

        String id = getPara();
        AttachmentVideo video = attachmentVideoService.findById(id);

        //视频云类型
        String containerCloudType = null;

        if (StrUtil.isNotBlank(video.getCloudType())) {
            containerCloudType = video.getCloudType();
        } else {
            containerCloudType = JPressOptions.get("attachment_cloud_type");
        }

        setAttr("containerCloudType", containerCloudType);

        String cloudType = JPressOptions.get("attachment_cloud_type");
        setAttr("cloudType", cloudType);

        String options = video.getOptions();
        if (StrUtil.isNotBlank(options)) {
            Map<String, String> map = JsonUtil.get(options, "", TypeDef.MAP_STRING);
            setAttr("options", map);
        }

        if (AttachmentVideo.CLOUD_TYPE_ALIYUN.equals(video.getCloudType())) {
            String playauth = AliyunVideoUtil.getPlayAuth(video.getVodVid());
            //阿里云
            //点播视频
            setAttr("cloudPlayAuth", playauth);
            setAttr("cloudVid", video.getVodVid());
            //直播回放
            setAttr("liveCloudPlayAuth", playauth);
        }
        //直播, m3u8 是延迟最高（延迟在 40s 左右）的，但是浏览器的兼容性是最好的
        setAttr("livePlayUrl", AliyunLiveUtil.createPlayUrlForM3U8(video.getLiveStream()));

        setAttr("liveCloudVid", video.getVodVid());

        //腾讯云
        //腾讯云点播视频：appId
        String appId = JPressOptions.get("attachment_qcloudvideo_appid");
        setAttr("appId", appId);

        String streamName = video.getLiveStream();

        //播放地址
        String playUrl = QCloudLiveUtil.createPlayUrlForM3U8(streamName);
        setAttr("playUrl", playUrl);
        String playUrlFlv = QCloudLiveUtil.createPlayUrlForFlv(streamName);
        setAttr("playUrlFlv", playUrlFlv);

        setAttr("video", video);

        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(), "order_number asc,id desc");
        setAttr("categories", categories);

        render("attachment/video_add.html");
    }

    public void add() {
        //视频播放容器
        String containerCloudType = JPressOptions.get("attachment_cloud_type");
        setAttr("containerCloudType", containerCloudType);
        //视频云类型
        String cloudType = JPressOptions.get("attachment_cloud_type");
        setAttr("cloudType", cloudType);

        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(), "order_number asc,id desc");
        setAttr("categories", categories);


        render("attachment/video_add.html");
    }

    @EmptyValidate({
            @Form(name = "video.vod_name", message = "视频标题不能为空")
    })
    public void doSave() {
        AttachmentVideo video = getModel(AttachmentVideo.class, "video");

        //新增
        if (video.getId() == null) {
            //uuid只设置一次
            video.setUuid(StrUtil.uuid());
        }

        String cloudType = JPressOptions.get("attachment_cloud_type");
        video.setCloudType(cloudType);

        Map<String, String> optionParas = getOptionParas();
        String json = JSON.toJSONString(optionParas);
        video.setOptions(json);

        attachmentVideoService.saveOrUpdate(video);
        //更新视频分类下的内容数量
        videoCategoryService.doUpdateVideoCategoryCount(video.getCategoryId());
        renderOkJson();
    }

    protected Map<String, String> getOptionParas() {
        Map<String, String> paras = getParas();
        Map<String, String> retMap = null;
        if (paras != null && !paras.isEmpty()) {
            retMap = new HashMap<>();
            for (Map.Entry<String, String> e : paras.entrySet()) {
                if (e.getKey() != null && e.getKey().startsWith("option.")) {
                    retMap.put(e.getKey().substring(7), e.getValue());
                }
            }
        }
        return retMap == null || retMap.isEmpty() ? null : retMap;
    }


    public void doGetVideoInfo(String videoId) {

        //视频点播
        CloudVideoInfo videoInfo = AliyunVideoUtil.getVideoInfo(videoId);
        renderJson(Ret.ok()
                .set("videoId", videoInfo.getVideoId())
                .set("size", videoInfo.getSize()));
    }


    //    @AdminMenu(text = "视频分类", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 39)
    public void category() {
        List<AttachmentVideoCategory> categories = videoCategoryService.findListByColumns(Columns.create(), "order_number asc,id desc");
        setAttr("categories", categories);
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
        videoCategoryService.doUpdateVideoCategoryCount(category.getId());
        renderOkJson();
    }


    public void doCategoryDel() {
        videoCategoryService.deleteById(getIdPara());
        renderOkJson();
    }

    public void doDel() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }

        AttachmentVideo attachmentVideo = attachmentVideoService.findById(id);
        if (attachmentVideo == null) {
            renderError(404);
            return;
        }

        if (attachmentVideoService.delete(attachmentVideo)) {
            //删除本地的视频副本
            String options = attachmentVideo.getOptions();
            if (StrUtil.isNotBlank(options)) {
                Map<String, String> map = JsonUtil.get(options, "", TypeDef.MAP_STRING);
                if (map != null) {
                    String path = map.get("local_video_url");
                    File attachmentFile = AttachmentUtils.file(path);
                    if (attachmentFile.exists() && attachmentFile.isFile()) {
                        attachmentFile.delete();
                        AliyunOssUtils.delete(new StringBuilder(path).delete(0, 1).toString());
                    }
                }
            }
        }

        renderOkJson();
    }

    /**
     * 批量删除视频
     */
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        Object[] array = idsSet.toArray();

        if (array == null || array.length <= 0) {
            renderError(404);
            return;
        }

        for (Object id : array) {
            AttachmentVideo attachmentVideo = attachmentVideoService.findById(id);
            if (attachmentVideo == null) {
                renderError(404);
                return;
            }

            if (attachmentVideoService.delete(attachmentVideo)) {
                //删除本地的视频副本
                String options = attachmentVideo.getOptions();
                if (StrUtil.isNotBlank(options)) {
                    Map<String, String> map = JsonUtil.get(options, "", TypeDef.MAP_STRING);
                    if (map != null) {
                        String path = map.get("local_video_url");
                        File attachmentFile = AttachmentUtils.file(path);
                        if (attachmentFile.exists() && attachmentFile.isFile()) {
                            attachmentFile.delete();
                            AliyunOssUtils.delete(new StringBuilder(path).delete(0, 1).toString());
                        }
                    }
                }
            }
        }
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
        if (authMap == null) {
            renderFailJson("请先配置阿里云点播视频的密钥!");
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
        if (authMap == null) {
            renderFailJson("请先配置阿里云点播视频的密钥!");
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
     * 腾讯云 获取签名
     *
     * @return
     */
    public String getSign() {
        String secretId = JPressOptions.get("attachment_qcloudvideo_secretid");
        String secretKey = JPressOptions.get("attachment_qcloudvideo_secretkey");

        //得到Sign
        Signature sign = new Signature();
        //个人API密钥中的Secret Id
        sign.setSecretId(secretId);
        //个人API密钥中的Secret Key
        sign.setSecretKey(secretKey);
        sign.setCurrentTime(System.currentTimeMillis() / 1000);
        sign.setRandom(new Random().nextInt(java.lang.Integer.MAX_VALUE));
        sign.setSignValidDuration(3600 * 24 * 2);

        String signature = null;
        try {
            signature = sign.getUploadSignature();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }

    /**
     * 腾讯云 使用任务流模板进行视频处理
     *
     * @param fileId
     */
    public void setTaskStream(String fileId) throws Exception {
        String procedureResponse = QCloudVideoUtil.setAdaptiveBitstream(fileId);

        render(Ret.ok().set("procedureResponse", procedureResponse));
    }


    /**
     * 腾讯云 创建直播流
     *
     * @return
     */
    public Ret doQCloudCreateLive() throws Exception {
        String streamName = StrUtil.uuid();

        String appName = QCloudLiveUtil.getAppName();
        String playDomain = QCloudLiveUtil.getPlayDomain();

        String pushUrl = QCloudLiveUtil.createPushUrl(streamName);
        String playUrl = QCloudLiveUtil.createPlayUrlForM3U8(streamName);
        String playUrlFlv = QCloudLiveUtil.createPlayUrlForFlv(streamName);

        return Ret.ok().set("pushUrl", pushUrl).set("playUrl", playUrl).set("liveApp", appName)
                .set("domainName", playDomain).set("streamName", streamName).set("playUrlFlv", playUrlFlv);
    }


    /**
     * 选择视频
     */
    public void browse() {

        String categoryId = getPara("categoryId");
        setAttr("categoryId", categoryId);

        //腾讯云点播视频：appId
        String appId = JPressOptions.get("attachment_qcloudvideo_appid");
        setAttr("appId", appId);

        Page<AttachmentVideo> page = attachmentVideoService.paginateByColumns(getPagePara(), getPageSizePara(),
                Columns.create("category_id", categoryId).orEqs("video_type"
                        , AttachmentVideo.VIDEO_TYPE_VIDEO
                        , AttachmentVideo.VIDEO_TYPE_LOCAL), "id desc");

        setAttr("page", page);

        List<AttachmentVideoCategory> categories = videoCategoryService.findAll();
        setAttr("categories", categories);

        render("attachment/vod_browse.html");
    }


    /**
     * 本地视频上传
     */
    public void uploadLocalVideo() {

        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("message", "请选择要上传的文件"));
            return;
        }


        File file = uploadFile.getFile();
        if (!getLoginedUser().isStatusOk()) {
            AttachmentUtils.delete(uploadFile.getFile());
            renderJson(Ret.of("error", Ret.of("message", "当前用户未激活，不允许上传任何文件。")));
            return;
        }

        if (AttachmentUtils.isUnSafe(file)) {
            AttachmentUtils.delete(uploadFile.getFile());
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        int maxSize = JPressOptions.getAsInt("attachment_other_maxsize", 100);

        int fileSize = Math.round(file.length() / 1024 * 100) / 100;
        if (maxSize > 0 && fileSize > maxSize * 1024) {
            AttachmentUtils.delete(uploadFile.getFile());
            renderJson(Ret.fail().set("message", "上传视频大小不能超过 " + maxSize + " MB"));
            return;
        }

        String path = AttachmentUtils.moveFile(uploadFile);
        AliyunOssUtils.upload(path, AttachmentUtils.file(path));

        String src = path.replace("\\", "/");


        renderJson(Ret.ok().set("success", true).set("src", src).set("fileName", file.getName()));
    }


    /**
     * 获取视频信息
     */
    public void getVideoInfo() {
        String uuid = getPara("id");

        if (StrUtil.isBlank(uuid)) {
            renderJson(Ret.fail().set("message", "传入的视频uuid为空！"));
            return;
        }

        AttachmentVideo video = attachmentVideoService.findByUuid(uuid);
        if (video == null) {
            renderJson(Ret.fail().set("message", "视频信息为空！"));
            return;
        }


        if (AttachmentVideo.CLOUD_TYPE_ALIYUN.equals(video.getCloudType())) {//阿里云

            //视频云端id
            if (StrUtil.isBlank(video.getVodVid())) {
                renderJson(Ret.fail().set("message", "阿里云 视频云端id为空！"));
                return;
            }
            //阿里云视频播放凭证
            String playAuth = AliyunVideoUtil.getPlayAuth(video.getVodVid());
            if (StrUtil.isBlank(playAuth)) {
                renderJson(Ret.fail().set("message", "阿里云视频播放凭证为空！"));
                return;
            }

            renderJson(Ret.ok().set("success", true).set("vid", video.getVodVid()).set("playAuth", playAuth).set("cloudType", video.getCloudType()));


        } else if (AttachmentVideo.CLOUD_TYPE_QCLOUD.equals(video.getCloudType())) {//腾讯云

            String appId = JPressOptions.get("attachment_qcloudvideo_appid");
            if (StrUtil.isBlank(appId)) {
                renderJson(Ret.fail().set("message", "请配置腾讯云的账号id"));
                return;
            }

            //视频云端id
            if (StrUtil.isBlank(video.getVodVid())) {
                renderJson(Ret.fail().set("message", "腾讯云 视频云端id为空！"));
                return;
            }

            renderJson(Ret.ok().set("success", true).set("vid", video.getVodVid()).set("aid", appId).set("cloudType", video.getCloudType()));

        } else if (AttachmentVideo.CLOUD_TYPE_LOCAL.equals(video.getCloudType())) {//本地视频

            String options = video.getOptions();
            if (StrUtil.isNotBlank(options)) {
                Map<String, String> map = JsonUtil.get(options, "", TypeDef.MAP_STRING);
                if (map == null) {
                    renderJson(Ret.fail().set("message", "该视频类型是本地视频，请先上传本地视频！"));
                    return;
                }
                String src = map.get("local_video_url");
                renderJson(Ret.ok().set("success", true).set("src", src).set("cloudType", video.getCloudType()));

            }

        }

    }

}

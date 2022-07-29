package io.jpress.module.form.controller.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.TemplateControllerBase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/form")
public class FormController extends TemplateControllerBase {

    private static final String DEFAULT_FORM_DETAIL_TEMPLATE = "/WEB-INF/views/front/form/detail.html";
    private static final String DEFAULT_FORM_DETAIL_ARTICLE = "/WEB-INF/views/front/form/form_article_detail.html";


    @Inject
    private FormInfoService formInfoService;


    @Inject
    private FormDataService formDataService;


    public void index() {
        Long formId = getParaToLong();
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

        render("form.html", DEFAULT_FORM_DETAIL_TEMPLATE);
    }


    /**
     * 提交数据到 form
     */
    public void postData() {
        Long formId = getParaToLong();
        if (formId == null) {
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findById(formId);
        if (formInfo == null || !formInfo.isPublished()) {
            renderError(404);
            return;
        }

        try {
            // parseRequestToRecord 可能会出现数据转换异常，需要告知前端
            Record record = formInfo.parseRequestToRecord(getRequest());
            formDataService.save(formInfo.getCurrentTableName(), record);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail().set("message", e.getMessage()));
            return;
        }


        renderOkJson();
    }


    public void upload(){
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
//        if (!getLoginedUser().isStatusOk()) {
//            file.delete();
//            renderJson(Ret.of("error", Ret.of("message", "当前用户未激活，不允许上传任何文件。")));
//            return;
//        }

        if (AttachmentUtils.isUnSafe(file)) {
            file.delete();
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        String mineType = uploadFile.getContentType();
        String fileType = mineType.split("/")[0];

        int maxImgSize = JPressOptions.getAsInt("attachment_img_maxsize", 10);
        int maxOtherSize = JPressOptions.getAsInt("attachment_other_maxsize", 100);

        Integer maxSize = "image".equals(fileType) ? maxImgSize : maxOtherSize;

        int fileSize = Math.round(file.length() / 1024 * 100) / 100;
        if (maxSize > 0 && fileSize > maxSize * 1024) {
            file.delete();
            renderJson(Ret.fail().set("message", "上传文件大小不能超过 " + maxSize + " MB"));
            return;
        }

        String path = AttachmentUtils.moveFile(uploadFile);
        AliyunOssUtils.upload(path, AttachmentUtils.file(path));

//        //附件分类id
//        Integer categoryId = getParaToInt("categoryId");
//
//        Attachment attachment = new Attachment();
//        attachment.setUserId(getLoginedUser().getId());
//        attachment.setCategoryId(categoryId);
//        attachment.setTitle(uploadFile.getOriginalFileName());
//        attachment.setPath(path.replace("\\", "/"));
//        attachment.setSuffix(FileUtil.getSuffix(uploadFile.getFileName()));
//        attachment.setMimeType(uploadFile.getContentType());
//
//        Object attachmentId = service.save(attachment);
//        //更新分类下的内容数量
//        if(attachment.getCategoryId() != null){
//            categoryService.doUpdateAttachmentCategoryCount(attachment.getCategoryId().longValue());
//        }

        renderJson(Ret.ok().set("success", true)
                .set("src", path.replace("\\", "/"))
        );
    }

    /**
     * 获取表单数据
     */
    public void detail() {

        Long formId = getParaToLong();

        FormInfo formInfo = formInfoService.findById(formId);

        if (formId == null || formInfo == null) {
            renderError(404);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("formId", formId);


        String resultHtml = renderToString(DEFAULT_FORM_DETAIL_ARTICLE, map);

        map.put("state", true);
        map.put("html", resultHtml);

        renderJson(map);

    }


}

package io.jpress.module.form.controller.front;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.module.form.model.FieldInfo;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.*;

@RequestMapping("/form")
public class FormController extends TemplateControllerBase {

    private static final String DEFAULT_FORM_DETAIL_TEMPLATE = "/WEB-INF/views/front/form/detail.html";
    private static final String DEFAULT_FORM_DETAIL_ARTICLE = "/WEB-INF/views/front/form/form_article_detail.html";


    @Inject
    private FormInfoService formInfoService;


    @Inject
    private FormDataService formDataService;


    @Inject
    private CaptchaService captchaService;


    public void index() {
        String uuid = getPara();

        if (uuid == null) {
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findByUUID(uuid);

        if (formInfo == null || !formInfo.isPublished()) {
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
        String uuid = getPara();

        if (uuid == null) {
            renderError(404);
            return;
        }


        List<UploadFile> files = null;
        if (RequestUtil.isMultipartRequest(getRequest())){
            files = getFiles();
        }

        CaptchaVO captchaVO = getBean(CaptchaVO.class);

        //进行前端滑块 参数验证
        if (captchaVO == null || captchaVO.getCaptchaVerification() == null) {
            if (files != null) {
                files.forEach(FileUtil::delete);
            }
            renderFailJson("验证失败");
            return;
        }


        ResponseModel validResult = captchaService.verification(captchaVO);
        if (validResult == null || !validResult.isSuccess()) {
            if (files != null) {
                files.forEach(FileUtil::delete);
            }
            renderFailJson("验证失败");
            return;
        }

        FormInfo formInfo = formInfoService.findByUUID(uuid);
        if (formInfo == null || !formInfo.isPublished()) {
            renderError(404);
            return;
        }


        Set<String> uploadFileNames = new HashSet<>();
        List<FieldInfo> fieldInfos = formInfo.getFieldInfos();
        if (fieldInfos != null) {
            for (FieldInfo fieldInfo : fieldInfos) {
                if (fieldInfo.isSupportUpload()) {
                    uploadFileNames.add(fieldInfo.getParaName());
                }
            }
        }


        try {
            // parseRequestToRecord 可能会出现数据转换异常，需要告知前端
            Record record = formInfo.parseRequestToRecord(getRequest());
            Map<String, String> uploadFilePaths = getFilePaths(uploadFileNames);
            for (FieldInfo fieldInfo : fieldInfos) {
                if (fieldInfo.isSupportUpload()) {
                    record.set(fieldInfo.getFieldName(),uploadFilePaths.get(fieldInfo.getParaName()));
                }
            }

            formDataService.save(formInfo.getCurrentTableName(), record);

            //更新表单 的 数据数量 和 最后数据时间
            Integer dataCount = formDataService.findCountByTable(formInfo.getCurrentTableName());
            formInfo.setDataCount(dataCount);
            formInfo.setDataCreated(new Date());
            formInfo.update();

        } catch (IllegalArgumentException iae) {
            renderJson(Ret.fail().set("message", iae.getMessage()));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail().set("message", "数据提交失败，请联系管理员"));
            return;
        }

        renderOkJson();
    }

    private Map<String, String> getFilePaths(Set<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("names can not be null or empty.");
        }

        Map<String, String> filesMap = new HashMap<>();
        List<UploadFile> uploadFiles = getFiles();
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return filesMap;
        }

        for (UploadFile uploadFile : uploadFiles) {
            String parameterName = uploadFile.getParameterName();
            if (StrUtil.isBlank(parameterName)) {
                FileUtil.delete(uploadFile);
                continue;
            }

            parameterName = matchedParameterName(parameterName.trim(),names);

            if (StrUtil.isNotBlank(parameterName)) {
                String path = AttachmentUtils.moveFile(uploadFile);
                AliyunOssUtils.upload(path, AttachmentUtils.file(path));

                String orignalPath = filesMap.get(parameterName);
                if (orignalPath == null){
                    orignalPath = path;
                }else {
                    orignalPath = orignalPath+";"+path;
                }
                filesMap.put(parameterName, orignalPath);
            } else {
                FileUtil.delete(uploadFile);
            }
        }

        return filesMap;
    }


    private String matchedParameterName(String paraName,Set<String> names){
        if (names.contains(paraName)){
            return paraName;
        }

        int indexOf = paraName.lastIndexOf("_");
        if (indexOf > 0 && StrUtil.isNumeric(paraName.substring(indexOf + 1))){
            paraName = paraName.substring(0,indexOf);
            if (names.contains(paraName)){
                return paraName;
            }
        }

        return null;
    }



    /**
     * 获取表单数据
     */
    public void detail() {
        Map<String, Object> map = new HashMap<>();
        map.put("state", true);

        String resultHtml = "";

        String uuid = getPara();


        if (uuid != null) {
            FormInfo formInfo = formInfoService.findByUUID(uuid);
            if (formInfo != null && formInfo.isPublished()) {
                setAttr("form", formInfo);
                resultHtml = renderToString(DEFAULT_FORM_DETAIL_ARTICLE, map);
            }
        }

        map.put("html", resultHtml);
        renderJson(map);
    }


}

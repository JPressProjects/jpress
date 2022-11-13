package io.jpress.module.form.controller.front;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
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

    private static final String DEFAULT_FORM_INSERT_TEMPLATE = "/WEB-INF/views/front/form/form_insert.html";
    private static final String DEFAULT_FORM_SUBMIT_TEMPLATE = "/WEB-INF/views/front/form/form_submit.html";

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
        render("form.html", DEFAULT_FORM_SUBMIT_TEMPLATE);
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
        if (RequestUtil.isMultipartRequest(getRequest())) {
            files = getFiles();
        }

        CaptchaVO captchaVO = getBean(CaptchaVO.class);

        //进行前端滑块 参数验证
        if (captchaVO == null || captchaVO.getCaptchaVerification() == null) {
            deleteFiles(files);
            renderFailJson("验证失败");
            return;
        }


        ResponseModel validResult = captchaService.verification(captchaVO);
        if (validResult == null || !validResult.isSuccess()) {
            deleteFiles(files);
            renderFailJson("验证失败");
            return;
        }

        FormInfo formInfo = formInfoService.findByUUID(uuid);
        if (formInfo == null || !formInfo.isPublished()) {
            deleteFiles(files);
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


            //有上传文件
            if (uploadFileNames.size() > 0) {
                Map<String, String> uploadFilePaths = getFilePaths(uploadFileNames);
                for (FieldInfo fieldInfo : fieldInfos) {
                    if (fieldInfo.isSupportUpload()) {
                        record.set(fieldInfo.getFieldName(), uploadFilePaths.get(fieldInfo.getParaName()));
                    }
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
        }finally {
            if (files != null) {
                for (UploadFile file : files) {
                    if (file.getFile().exists() && !file.getFile().delete()){
                        LogKit.error("Can not delete file: {}",file.getFile().getAbsolutePath());
                    }
                }
            }
        }

        renderOkJson();
    }

    private void deleteFiles(List<UploadFile> files) {
        if (files != null) {
            files.forEach(FileUtil::delete);
        }
    }


    private Map<String, String> getFilePaths(Set<String> names) {

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

            parameterName = matchedParameterName(parameterName.trim(), names);

            if (StrUtil.isNotBlank(parameterName)) {
                String path = AttachmentUtils.moveFile(uploadFile);
                AliyunOssUtils.upload(path, AttachmentUtils.file(path));

                String exitsPath = filesMap.get(parameterName);
                if (exitsPath == null) {
                    exitsPath = path;
                } else {
                    exitsPath = exitsPath + ";" + path;
                }
                filesMap.put(parameterName, exitsPath);
            } else {
                FileUtil.delete(uploadFile);
            }
        }

        return filesMap;
    }


    /**
     * JFinal 有一个 bug，相同 name 属性的文件选择框，第二个 name 会添加索引 _0,_1....
     * 这个方法用来匹配到真正的 name
     *
     * @param paraName
     * @param names
     * @return
     */
    private String matchedParameterName(String paraName, Set<String> names) {
        if (names.contains(paraName)) {
            return paraName;
        }

        int indexOf = paraName.lastIndexOf("_");
        if (indexOf > 0 && StrUtil.isNumeric(paraName.substring(indexOf + 1))) {
            paraName = paraName.substring(0, indexOf);
            if (names.contains(paraName)) {
                return paraName;
            }
        }

        return null;
    }


    /**
     * 获取表单数据
     * 用于插入表单到文章和页面等
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
                resultHtml = renderToString(DEFAULT_FORM_INSERT_TEMPLATE, map);
            }
        }

        map.put("html", resultHtml);
        renderJson(map);
    }


}

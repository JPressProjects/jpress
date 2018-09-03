package io.jpress.web.admin;

import com.jfinal.upload.UploadFile;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.web.base.AdminControllerBase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/option")
public class _OptionController extends AdminControllerBase {


    public void save() {

        HashMap<String, UploadFile> filesMap = getUploadFilesMap();

        HashMap<String, String> datasMap = new HashMap<String, String>();

        Map<String, String[]> paraMap = getParaMap();
        if (paraMap != null && !paraMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : paraMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    String value = null;
                    for (String v : entry.getValue()) {
                        if (StringUtils.isNotEmpty(v)) {
                            value = v;
                            break;
                        }
                    }
                    datasMap.put(entry.getKey(), value);
                }
            }
        }

        String autosaveString = getPara("autosave");
        if (StringUtils.isNotBlank(autosaveString)) {
            String[] keys = autosaveString.split(",");
            for (String key : keys) {
                if (StringUtils.isNotBlank(key) && !datasMap.containsKey(key)) {
                    datasMap.put(key.trim(), getRequest().getParameter(key.trim()));
                }
            }
        }

//        if(filesMap!=null && !filesMap.isEmpty()){
//            datasMap.putAll(filesMap);
//        }
//
//        for (Map.Entry<String, String> entry : datasMap.entrySet()) {
//            OptionQuery.me().saveOrUpdate(entry.getKey(), entry.getValue());
//        }
//
//        MessageKit.sendMessage(Actions.SETTING_CHANGED, datasMap);
//        renderAjaxResultForSuccess();
    }


//    public HashMap<String, String> getUploadFilesMap() {
//        List<UploadFile> fileList = null;
//        if (isMultipartRequest()) {
//            fileList = getFiles();
//        }
//
//        HashMap<String, String> filesMap = null;
//        if (fileList != null) {
//            filesMap = new HashMap<String, String>();
//            for (UploadFile ufile : fileList) {
//                String filePath = AttachmentUtils.moveFile(ufile).replace("\\", "/");
//                filesMap.put(ufile.getParameterName(), filePath);
//            }
//        }
//        return filesMap;
//    }


}

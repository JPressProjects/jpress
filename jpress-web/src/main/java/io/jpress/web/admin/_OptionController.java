package io.jpress.web.admin;

import com.jfinal.kit.Ret;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.service.OptionService;

import javax.inject.Inject;
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


    @Inject
    private OptionService os;

    public void save() {

        Map<String, String[]> paraMap = getParaMap();
        if (paraMap == null || paraMap.isEmpty()) {
            renderJson(Ret.fail("msg", "para is empty"));
            return;
        }


        HashMap<String, String> datasMap = new HashMap<String, String>();
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


        for (Map.Entry<String, String> entry : datasMap.entrySet()) {
            os.saveOrUpdate(entry.getKey(), entry.getValue());
        }

        renderJson(Ret.ok());
    }


}

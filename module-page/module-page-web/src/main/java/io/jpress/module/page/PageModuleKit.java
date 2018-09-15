package io.jpress.module.page;

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jpress.JPressConstants;
import io.jpress.module.page.model.SinglePage;
import io.jpress.service.OptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.module.page
 */
public class PageModuleKit {

    public static String pageUrl(SinglePage page) {
        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return page.getUrl("");
        }

        String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StringUtils.isBlank(suffix) ? page.getUrl("") : page.getUrl(suffix);
    }
}

package io.jpress.module.page.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressConstants;
import io.jpress.module.page.model.SinglePage;
import io.jpress.service.OptionService;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("pageUrl")
public class PageUrlDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        SinglePage page = getParam(0, scope);

        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            render(writer, page.getUrl(""));
            return;
        }

        String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
        render(writer, StringUtils.isBlank(suffix) ? page.getUrl("") : page.getUrl(suffix));
    }

    private void render(Writer writer, String content) {
        try {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

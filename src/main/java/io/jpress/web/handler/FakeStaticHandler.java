package io.jpress.web.handler;

import com.google.inject.Inject;
import com.jfinal.handler.Handler;
import io.jboot.utils.StringUtils;
import io.jpress.Constants;
import io.jpress.service.OptionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 处理伪静态的 handler
 * @Description: 负责处理 后台设置的 伪静态功能
 * @Package io.jpress.web.admin
 */
public class FakeStaticHandler extends Handler {

    @Inject
    private OptionService optionService;

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        // 启用伪静态
        if (optionService.isTrue(Constants.OPTION.FAKE_STATIC_ENABLE)) {
            String setPostfix = optionService.findValue(Constants.OPTION.FAKE_STATIC_POSTFIX);
            setPostfix = StringUtils.isBlank(setPostfix) ? ".html" : setPostfix;
            int index = target.lastIndexOf(setPostfix);
            if (index != -1) {
                target = target.substring(0, index);
            }
            this.next.handle(target, request, response, isHandled);
        }

        /**
         * 如果没有启用 伪静态
         */
        else {
            next.handle(target, request, response, isHandled);
        }
    }
}

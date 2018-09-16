package io.jpress.web.handler;


import com.jfinal.handler.Handler;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventListener;
import io.jboot.event.annotation.EventConfig;
import io.jboot.utils.StringUtils;
import io.jpress.JPressConstants;
import io.jpress.model.Option;
import io.jpress.service.OptionService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 伪静态处理器
 * @Package io.jpress.web.handler
 */

@EventConfig(action = JPressConstants.EVENT_OPTION_UPDATE)
public class FakeStaticHandler extends Handler implements JbootEventListener {

    private static String suffix = null;

    public static void initSuffix(String suffix) {
        FakeStaticHandler.suffix = suffix;
    }

    @Inject
    private OptionService optionService;

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (StringUtils.isBlank(suffix)) {
            next.handle(target, request, response, isHandled);
            return;
        }
        if (target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
        }
        next.handle(target, request, response, isHandled);
    }

    @Override
    public void onEvent(JbootEvent event) {
        Option option = event.getData();

        /**
         * 伪静态开关的设置
         */
        if (JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE.equals(option.getKey())) {

            Boolean enable = optionService.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
            if (enable == null || enable == false) {
                initSuffix(null);
                return;
            }

            String suffix = optionService.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
            if (StringUtils.isBlank(suffix)) {
                initSuffix(null);
            } else {
                initSuffix(suffix);
            }

        }

        /**
         * 伪静态后缀的设置
         */
        else if (JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX.equals(option.getKey())) {
            Boolean enable = optionService.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
            if (enable == null || enable == false) {
                return;
            }

            String suffix = optionService.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
            if (StringUtils.isBlank(suffix)) {
                initSuffix(null);
            } else {
                initSuffix(suffix);
            }
        }

    }
}

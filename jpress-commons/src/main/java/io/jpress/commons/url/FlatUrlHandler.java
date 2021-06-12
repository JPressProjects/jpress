package io.jpress.commons.url;

import com.jfinal.handler.Handler;
import io.jpress.JPressOptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Predicate;

/**
 * JPress 网站的 URL 管理器
 * 主要是用于：
 * 1、处理伪静态的问题
 * 2、处理扁平化 URL 的问题，也就是全站 URL 转为为 /xx-xx-1.html ，不再有 "/" 这个符号 ，更加有利于 SEO
 */
public class FlatUrlHandler extends Handler {

    private static Set<FlatUrlProcesser> flatUrlProcessers = Collections.synchronizedSet(new HashSet<>());


    public static void addProcesser(FlatUrlProcesser processer) {
        flatUrlProcessers.add(processer);
    }


    public static void removeProcesser(Predicate<? super FlatUrlProcesser> filter) {
        flatUrlProcessers.removeIf(filter);
    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (JPressOptions.isFlatUrlEnable() && flatUrlProcessers.size() > 0) {
            for (FlatUrlProcesser processer : flatUrlProcessers) {
                target = processer.flat(target, request);
            }
        }
        next.handle(target, request, response, isHandled);
    }


}

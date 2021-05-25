package io.jpress.commons.url;


import javax.servlet.http.HttpServletRequest;

/**
 * Flat Url 处理器，其作用是对 target 进行转换
 * 例如：把 /article-1.html 还原到 /article/1.html
 */
public interface FlatUrlProcesser {

    String flat(String target, HttpServletRequest request);
}

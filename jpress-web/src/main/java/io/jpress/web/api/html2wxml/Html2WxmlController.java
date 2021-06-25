package io.jpress.web.api.html2wxml;

import com.jfinal.kit.StrKit;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.web.base.ApiControllerBase;

import java.util.Collections;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * 把 html 内容转化为 微信的 wxml 用于显示文章详情、产品详情等
 */
@RequestMapping("/api/wechat/mp/html2wxml")
public class Html2WxmlController extends ApiControllerBase {

    public void index() {
        String html = getPara("html"); // 要求html内容必须通过post传过来
        Params params = getParams();
        String resultJson = HtmlToJson.by(html, params).get();

        if (StrKit.isBlank(resultJson)) {
            renderJson(Collections.emptyList());
        } else {
            renderJson(resultJson);
        }
    }


    /**
     * 获取转换条件
     *
     * @return
     */
    private Params getParams() {
        //类型 默认HMTL
        String type = getPara("type", Params.TYPE_HTML);

        //是否开启pre代码高亮 默认开启
        Boolean highlight = getParaToBoolean("highlight", true);

        //是否开启pre代码行号 默认开启
        Boolean linenums = getParaToBoolean("linenums", true);

        //获取a和img静态资源的根路径URL
        String baseUri = StrUtil.obtainDefault(JPressOptions.getCDNDomain(),getBaseUrl());

        Params params = new Params();
        params.setHighlight(highlight);
        params.setLinenums(linenums);
        params.setType(type);
        params.setBaseUri(baseUri);

        return params;
    }


}

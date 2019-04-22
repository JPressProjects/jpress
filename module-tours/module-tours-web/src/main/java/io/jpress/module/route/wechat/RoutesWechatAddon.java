package io.jpress.module.route.wechat;

import com.jfinal.aop.Inject;
import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonConfig;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import io.jpress.service.OptionService;

import java.util.List;

/**
 * 旅游线路快速搜索
 *
 * @author Eric.Huang
 * @date 2019-04-14 15:27
 * @package io.jpress.module.route
 **/

@WechatAddonConfig(
    id = "cn.ninemm.routes",
    title = "线路搜索",
    description = "输入 `线路关键词` 返回线路列表",
    author = "Eric")
public class RoutesWechatAddon implements WechatAddon {

    @Inject
    TRouteService routeService;
    @Inject
    OptionService optionService;

    /**
     * 用来匹配是否由该插件执行
     *
     * @param inMsg
     * @param msgController
     * @return
     */
    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        return StrUtil.notBlank(content);
    }

    /**
     * 执行回复逻辑
     *
     * @param inMsg
     * @param msgController
     */
    @Override
    public boolean onRenderMessage(InMsg inMsg, MsgController msgController) {

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();

        String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        if (StrUtil.isBlank(webDomain)) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            outTextMsg.setContent("服务器配置错误：网站域名配置为空，请先到 后台->系统->常规 配置网站域名");
            msgController.render(outTextMsg);
            return true;
        }

        List<TRoute> routes = routeService.searchByTitleInWechat(content, 10);
        if (routes == null) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            outTextMsg.setContent("没有找到对应的线路，请修改关键词");
            msgController.render(outTextMsg);
            return true;
        }

        OutNewsMsg out = new OutNewsMsg(inMsg);
        for (TRoute route : routes) {
            News news = new News();
            news.setTitle(route.getTitle());
            news.setDescription(route.getMetaDescription());
            news.setPicUrl(webDomain + route.getWechatThumbnail());
            if (StrUtil.notBlank(route.getLinkTo())) {
                news.setUrl(route.getUrl());
            } else {
                news.setUrl(webDomain + route.getUrl());
            }
            out.addNews(news);
        }
        msgController.render(out);
        return true;
    }
}

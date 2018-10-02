package io.jpress.web.admin.kits;

import io.jpress.model.WechatMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 工具类
 * @Description: 用于分类的层级关系
 * @Package io.jpress.module.article.kits
 */
public class WechatMenuKits {

    public static void toLayer(List<WechatMenu> wechatMenus) {
        toTree(wechatMenus);
        List<WechatMenu> temp = new ArrayList<>();
        fillTempByChild(temp, wechatMenus);
        wechatMenus.clear();
        wechatMenus.addAll(temp);
    }

    private static void fillTempByChild(List<WechatMenu> temp, List<WechatMenu> childs) {
        if (childs == null || childs.isEmpty()) {
            return;
        }

        for (WechatMenu category : childs) {
            temp.add(category);
            fillTempByChild(temp, category.getChilds());
        }
    }


    public static void toTree(List<WechatMenu> wechatMenus) {
        List<WechatMenu> temp = new ArrayList<>(wechatMenus);
        wechatMenus.clear();

        for (WechatMenu category : temp) {
            if (category.isTopCategory()) {
                category.setLayerNO(0);
                fillChild(category, temp);
                wechatMenus.add(category);
            }
        }
    }

    private static void fillChild(WechatMenu parent, List<WechatMenu> wechatMenus) {
        for (WechatMenu category : wechatMenus) {

            if (category.getPid() == parent.getId()) {

                category.setLayerNO(parent.getLayerNO() + 1);
                parent.addChild(category);

                fillChild(category, wechatMenus);
            }
        }
    }


}

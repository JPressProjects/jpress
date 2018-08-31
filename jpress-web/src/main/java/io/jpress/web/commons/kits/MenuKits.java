package io.jpress.web.commons.kits;

import com.google.common.collect.Lists;
import io.jpress.model.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 工具类
 * @Description: 用于菜单的层级关系
 * @Package io.jpress.web.commons.kits
 */
public class MenuKits {

    public static void toLayerCategories(List<Menu> menus) {
        toTreeCategories(menus);
        List<Menu> temp = new ArrayList<>();
        fillTempByChild(temp, menus);
        menus.clear();
        menus.addAll(temp);
    }

    private static void fillTempByChild(List<Menu> temp, List<Menu> childs) {
        if (childs == null || childs.isEmpty()) {
            return;
        }

        for (Menu category : childs) {
            temp.add(category);
            fillTempByChild(temp, category.getChilds());
        }
    }


    public static void toTreeCategories(List<Menu> menus) {
        List<Menu> temp = new ArrayList<>(menus);
        menus.clear();

        for (Menu menu : temp) {
            if (menu.isTopMenu()) {
                menu.setLayerNO(0);
                fillChild(menu, temp);
                menus.add(menu);
            }
        }
    }

    private static void fillChild(Menu parent, List<Menu> allCategories) {
        for (Menu category : allCategories) {

            if (category.getPid() == parent.getId()) {

                category.setLayerNO(parent.getLayerNO() + 1);
                parent.addChild(category);

                fillChild(category, allCategories);
            }
        }
    }


    public static void main(String[] args) {
        Menu c1 = new Menu();
        c1.setPid(0l);
        c1.setId(1l);
        c1.setText("c1");


        Menu c2 = new Menu();
        c2.setPid(0l);
        c2.setId(2l);
        c2.setText("c2");


        Menu c3 = new Menu();
        c3.setPid(0l);
        c3.setId(3l);
        c3.setText("c3");


        Menu c4 = new Menu();
        c4.setPid(1l);
        c4.setId(4l);
        c4.setText("c4");


        Menu c5 = new Menu();
        c5.setPid(3l);
        c5.setId(5l);
        c5.setText("c5");


        Menu c6 = new Menu();
        c6.setPid(3l);
        c6.setId(6l);
        c6.setText("c6");


        Menu c7 = new Menu();
        c7.setPid(5l);
        c7.setId(7l);
        c7.setText("c7");


        List<Menu> categories2 = Lists.newArrayList(c1, c2, c3, c4, c5, c6, c7);
        toLayerCategories(categories2);

        for (Menu category : categories2) {
            System.out.println(category.getText() + "-------layer:" + category.getLayerNO());
        }
    }
}

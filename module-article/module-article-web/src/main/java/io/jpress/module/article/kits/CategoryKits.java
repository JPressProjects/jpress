package io.jpress.module.article.kits;

import com.google.common.collect.Lists;
import io.jpress.module.article.model.ArticleCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 工具类
 * @Description: 用于分类的层级关系
 * @Package io.jpress.module.article.kits
 */
public class CategoryKits {

    public static void toLayerCategories(List<ArticleCategory> categories) {
        toTreeCategories(categories);
        List<ArticleCategory> temp = new ArrayList<>();
        fillTempByChild(temp, categories);
        categories.clear();
        categories.addAll(temp);
    }

    private static void fillTempByChild(List<ArticleCategory> temp, List<ArticleCategory> childs) {
        if (childs == null || childs.isEmpty()) {
            return;
        }

        for (ArticleCategory category : childs) {
            temp.add(category);
            fillTempByChild(temp, category.getChilds());
        }
    }


    public static void toTreeCategories(List<ArticleCategory> categories) {
        List<ArticleCategory> temp = new ArrayList<>(categories);
        categories.clear();

        for (ArticleCategory category : temp) {
            if (category.isTop()) {
                category.setLayerNO(0);
                fillChild(category, temp);
                categories.add(category);
            }
        }
    }

    private static void fillChild(ArticleCategory parent, List<ArticleCategory> allCategories) {
        for (ArticleCategory category : allCategories) {

            if (category.getPid() == parent.getId()) {

                category.setLayerNO(parent.getLayerNO() + 1);
                parent.addChild(category);

                fillChild(category, allCategories);
            }
        }
    }


    public static void main(String[] args) {
        ArticleCategory c1 = new ArticleCategory();
        c1.setPid(0l);
        c1.setId(1l);
        c1.setTitle("c1");


        ArticleCategory c2 = new ArticleCategory();
        c2.setPid(0l);
        c2.setId(2l);
        c2.setTitle("c2");


        ArticleCategory c3 = new ArticleCategory();
        c3.setPid(0l);
        c3.setId(3l);
        c3.setTitle("c3");


        ArticleCategory c4 = new ArticleCategory();
        c4.setPid(1l);
        c4.setId(4l);
        c4.setTitle("c4");


        ArticleCategory c5 = new ArticleCategory();
        c5.setPid(3l);
        c5.setId(5l);
        c5.setTitle("c5");


        ArticleCategory c6 = new ArticleCategory();
        c6.setPid(3l);
        c6.setId(6l);
        c6.setTitle("c6");


        ArticleCategory c7 = new ArticleCategory();
        c7.setPid(5l);
        c7.setId(7l);
        c7.setTitle("c7");

//        List<ArticleCategory> categories1 = Lists.newArrayList(c1, c2, c3, c4, c5, c6, c7);
//        toTreeCategories(categories1);
//
//        for (ArticleCategory category : categories1) {
//            System.out.println(category.getTitle());
//        }


        List<ArticleCategory> categories2 = Lists.newArrayList(c1, c2, c3, c4, c5, c6, c7);
        toLayerCategories(categories2);

        for (ArticleCategory category : categories2) {
            System.out.println(category.getTitle() + "-------layer:" + category.getLayerNO());
        }
    }
}

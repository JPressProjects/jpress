package io.jpress.spider;

import io.jpress.spider.inter.SpriderInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjw on 10/28/16.
 */
public class SpiderHandler {

    private static SpiderHandler mSpiderHandler;
    private static List<Class<?>> mSpiders = new ArrayList<>();

    public static SpiderHandler getSpiderHandler() {
        if (mSpiderHandler == null) {
            mSpiderHandler = new SpiderHandler();
            mSpiders.add(SMZDMPageProcessor.class);
        }
        return mSpiderHandler;
    }

    public void startSpiders() {
        for (Class c : mSpiders) {
            try {
                SpriderInterface spriderInterface = (SpriderInterface) c.newInstance();
                spriderInterface.spriderStart();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SpiderHandler.getSpiderHandler().startSpiders();
    }

}

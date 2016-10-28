package io.jpress.spider;

import io.jpress.spider.inter.SpriderInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjw on 10/28/16.
 */
public class SpiderHandler {

    private static SpiderHandler mSpiderHandler;
    private static List<Class<?>> mSpiders_class = new ArrayList<>();
    private static List<SpriderInterface> mSpiders = new ArrayList<>();

    public static SpiderHandler getSpiderHandler() {
        if (mSpiderHandler == null) {
            mSpiderHandler = new SpiderHandler();
            mSpiders_class.add(SMZDMPageProcessor.class);
        }
        return mSpiderHandler;
    }

    public void startSpiders() {
        for (Class c : mSpiders_class) {
            try {
                SpriderInterface spriderInterface = (SpriderInterface) c.newInstance();
                spriderInterface.spriderStart();
                mSpiders.add(spriderInterface);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        for (SpriderInterface spriderInterface : mSpiders) {
            if (spriderInterface.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SpiderHandler.getSpiderHandler().startSpiders();
    }

}

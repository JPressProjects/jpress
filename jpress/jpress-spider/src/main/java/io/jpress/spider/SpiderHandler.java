package io.jpress.spider;

import io.jpress.spider.inter.SpriderInterface;
import org.apache.commons.collections.CollectionUtils;

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
        initSpiderInstand();
        return mSpiderHandler;
    }

    private static void initSpiderInstand() {
        if (CollectionUtils.isEmpty(mSpiders)) {
            for (Class c : mSpiders_class) {
                try {
                    SpriderInterface spriderInterface = (SpriderInterface) c.newInstance();
                    mSpiders.add(spriderInterface);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startSpiders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {
                    return;
                }
                for (SpriderInterface spriderInterface : mSpiders) {
                    spriderInterface.spriderStart();
                }
            }
        }).start();
    }

    public void stopSpiders() {
        if (!isRunning()) {
            return;
        }
        for (SpriderInterface mSpider : mSpiders) {
            mSpider.spriderStop();
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

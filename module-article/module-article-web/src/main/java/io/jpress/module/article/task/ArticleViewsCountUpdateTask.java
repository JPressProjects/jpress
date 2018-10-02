package io.jpress.module.article.task;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.schedule.annotation.FixedRate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于更新文章的 评论 数量
 * @Package io.jpress.module.article.task
 */
@FixedRate(period = 5, initialDelay = 5)
public class ArticleViewsCountUpdateTask implements Runnable {

    private static Map<Long, AtomicLong> countsMap = new ConcurrentHashMap<>();

    public static void recordCount(Long id) {
        AtomicLong count = countsMap.get(id);
        if (count == null) {
            count = new AtomicLong(0);
            countsMap.put(id, count);
        }
        count.getAndIncrement();
    }


    @Override
    public void run() {
        if (countsMap.isEmpty()) {
            return;
        }

        Map<Long, AtomicLong> articleViews = new HashMap<>(countsMap);
        countsMap.clear();

        for (Map.Entry<Long, AtomicLong> entry : articleViews.entrySet()) {
            Db.update("update article set view_count = view_count + "
                    + entry.getValue().get()
                    + " where id = ? ", entry.getKey());
        }
    }
}

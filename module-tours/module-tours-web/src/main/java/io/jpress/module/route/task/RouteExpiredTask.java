package io.jpress.module.route.task;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.cron4j.ITask;
import io.jboot.db.model.Columns;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import org.joda.time.DateTime;

/**
 * 系统定时处理过期的线路
 * 处理时间：每天晚上 23:59
 *
 * @author Eric.Huang
 * @date 2019-03-12 16:18
 * @package io.jpress.module.route.task
 **/

public class RouteExpiredTask implements ITask {

    @Override
    public void stop() {

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        Aop.get(TRouteService.class).doUpdateExpiredRouteStatus();
    }
}

package io.jpress.service.task;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.schedule.annotation.FixedRate;
import io.jpress.model.Utm;

import java.util.ArrayList;
import java.util.List;


@FixedRate(period = 5, initialDelay = 5)
public class UtmBatchSaveTask implements Runnable {

    private static List<Utm> utmList = new ArrayList<>();

    public static void record(Utm utm) {
        utmList.add(utm);
    }


    @Override
    public void run() {
        if (utmList.isEmpty()) {
            return;
        }

        List<Utm> tempUtmList = new ArrayList<>(utmList);
        utmList.clear();

        Db.batchSave(tempUtmList, 1000);

    }

}
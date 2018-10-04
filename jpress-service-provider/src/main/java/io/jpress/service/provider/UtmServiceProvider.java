package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UtmService;
import io.jpress.model.Utm;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.task.UtmBatchSaveTask;

import javax.inject.Singleton;

@Bean
@Singleton
public class UtmServiceProvider extends JbootServiceBase<Utm> implements UtmService {

    @Override
    public void doRecord(Utm utm) {
        UtmBatchSaveTask.record(utm);
    }
}
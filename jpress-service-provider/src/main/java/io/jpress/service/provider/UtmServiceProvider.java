package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
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

    @Override
    public Page<Utm> _paginateByUserId(int page, int pagesize, long userId) {
        return DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), "created desc");
    }
}
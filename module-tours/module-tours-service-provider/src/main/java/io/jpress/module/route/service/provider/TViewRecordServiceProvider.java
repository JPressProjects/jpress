package io.jpress.module.route.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.module.route.service.TRouteService;
import io.jpress.module.route.service.TViewRecordService;
import io.jpress.module.route.model.TViewRecord;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.UserService;

@Bean
public class TViewRecordServiceProvider extends JbootServiceBase<TViewRecord> implements TViewRecordService {

    @Inject
    UserService userService;
    @Inject
    TRouteService routeService;

    @Override
    public Page<TViewRecord> paginate(int page, int pageSize) {
        Page<TViewRecord> _page = super.paginate(page, pageSize);
        userService.join(_page, "user_id");
        routeService.join(_page, "route_id", "route");
        return _page;
    }
}
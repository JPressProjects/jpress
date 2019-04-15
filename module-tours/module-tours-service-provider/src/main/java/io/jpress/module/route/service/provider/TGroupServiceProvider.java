package io.jpress.module.route.service.provider;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.DateUtils;
import io.jpress.module.route.model.TGroup;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.model.vo.CalendarVO;
import io.jpress.module.route.service.TGroupService;
import io.jpress.module.route.service.TRouteService;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

@Bean
public class TGroupServiceProvider extends JbootServiceBase<TGroup> implements TGroupService {

    @Inject
    TRouteService routeService;

    @Override
    public Page<TGroup> paginate(int page, int pageSize) {
        Page<TGroup> _page = super.paginate(page, pageSize);
        routeService.join(_page, "route_id", "route");
        return _page;
    }

    @Override
    @CacheEvict(name = "routes",key = "*")
    public void doUpdateGroups(TRoute route, Integer[] groups, String calendarStr) {

        if (groups == null) {
            return ;
        }

        Db.tx(() -> {
            Db.update("delete from t_group where route_id = ?", route.getId());
            Date created = new Date();
            List<TGroup> groupList = Lists.newArrayList();
            if (groups != null && groups.length > 0) {

                List<DateTime> list = null;
                for (Integer group : groups) {
                    // 天天出团
                    if (group == 0) {
                        list = DateUtils.getAllDaysBeforeExpireDate(route.getExpireDate());
                    } else {
                        list = DateUtils.dayOfWeek(group, route.getExpireDate());
                    }

                    int size = list.size();
                    for (int i = 0; i < size; i++) {

                        TGroup tgroup = new TGroup();
                        tgroup.setRouteId(route.getId());
                        tgroup.setLeaveDate(list.get(i).toDate());
                        tgroup.setDeadlineDate(DateUtils.plusDays(list.get(i).toDate(), route.getTotalDays()));

                        tgroup.setCost(route.getCost());
                        tgroup.setPrice(route.getPrice());
                        tgroup.setChildPrice(route.getChildPrice());
                        tgroup.setMarketPrice(route.getMarketPrice());

                        tgroup.setIsCalendar(0);
                        tgroup.setCreated(created);
                        int status = i == 0 ? TGroup.ENROLLING_STATUS : TGroup.UNSTART_STATUS;
                        tgroup.setStatus(status);

                        groupList.add(tgroup);
                    }
                }
            } else {
                List<CalendarVO> list = JSON.parseArray(calendarStr, CalendarVO.class);
                int size = list.size();
                for (int i = 0; i < size; i++) {

                    TGroup tgroup = new TGroup();
                    tgroup.setRouteId(route.getId());
                    Date leaveDate = DateUtils.strToDate(list.get(i).getStart(), DateUtils.DEFAULT_YEAR_MONTH_DAY);
                    tgroup.setLeaveDate(leaveDate);

                    Date deadlineDate = DateUtils.plusDays(leaveDate, route.getTotalDays());
                    tgroup.setDeadlineDate(deadlineDate);
                    List<String> routePrice = Splitter.on(",").splitToList(list.get(i).getTitle());
                    tgroup.setPrice(Integer.valueOf(routePrice.get(0)));

                    tgroup.setChildPrice(Integer.valueOf(routePrice.get(1)));
                    int status = i == 0 ? TGroup.ENROLLING_STATUS : TGroup.UNSTART_STATUS;
                    tgroup.setStatus(status);
                    tgroup.setCreated(created);
                    tgroup.setIsCalendar(1);

                    groupList.add(tgroup);
                }
            }
            Db.batchSave(groupList, groupList.size());
            return true;
        });
    }

    @Override
    public TGroup findFirstGroupByRouteId(Long routeId) {
        Columns columns = Columns.create("route_id", routeId);
        columns.add("status", TGroup.ENROLLING_STATUS);
        TGroup group = DAO.findFirstByColumns(columns, "id ");
        if (group == null) {
            return null;
        }
        return group;
    }

    @Override
    public TGroup findNextById(Long id) {
        return DAO.findFirst("select * from t_group where id > ?", id);
    }

}
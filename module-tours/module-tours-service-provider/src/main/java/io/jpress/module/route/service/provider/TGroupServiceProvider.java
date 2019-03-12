package io.jpress.module.route.service.provider;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.db.model.Column;
import io.jboot.service.JbootServiceBase;
import io.jpress.commons.utils.DateUtils;
import io.jpress.module.route.model.TGroup;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.model.vo.CalendarVO;
import io.jpress.module.route.service.TGroupService;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

@Bean
public class TGroupServiceProvider extends JbootServiceBase<TGroup> implements TGroupService {

    @Override
    @CacheEvict(name = "routes",key = "*")
    public void doUpdateGroups(TRoute route, Integer[] groups, String calendarStr) {

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

                    list.stream().forEach(obj -> {
                        TGroup tgroup = new TGroup();
                        tgroup.setRouteId(route.getId());
                        tgroup.setLeaveDate(obj.toDate());
                        tgroup.setDeadlineDate(DateUtils.plusDays(obj.toDate(), route.getTotalDays()));

                        tgroup.setCost(route.getCost());
                        tgroup.setPrice(route.getPrice());
                        tgroup.setChildPrice(route.getChildPrice());
                        tgroup.setMarketPrice(route.getMarketPrice());

                        tgroup.setCreated(created);
                        tgroup.setStatus(TGroup.DEFAULT_STATUS);
                        tgroup.setIsCalendar(0);
                        groupList.add(tgroup);
                    });
                }
            } else {
                List<CalendarVO> list = JSON.parseArray(calendarStr, CalendarVO.class);
                list.stream().forEach(obj -> {
                    TGroup tgroup = new TGroup();
                    tgroup.setRouteId(route.getId());

                    Date leaveDate = DateUtils.strToDate(obj.getStart(), DateUtils.DEFAULT_YEAR_MONTH_DAY);
                    tgroup.setLeaveDate(leaveDate);
                    Date deadlineDate = DateUtils.plusDays(leaveDate, route.getTotalDays());
                    tgroup.setDeadlineDate(deadlineDate);

                    List<String> routePrice = Splitter.on(",").splitToList(obj.getTitle());
                    tgroup.setPrice(Integer.valueOf(routePrice.get(0)));
                    tgroup.setChildPrice(Integer.valueOf(routePrice.get(1)));
                    tgroup.setStatus(TGroup.DEFAULT_STATUS);

                    tgroup.setCreated(created);
                    tgroup.setIsCalendar(1);
                    groupList.add(tgroup);
                });
            }
            Db.batchSave(groupList, groupList.size());
            return true;
        });
    }

    @Override
    public Long findCurGroupByRouteId(Long routeId) {
        Column column = Column.create("route_id", routeId);
        TGroup group = DAO.findFirstByColumn(column, "id ");
        if (group == null) {
            return null;
        }
        return group.getId();
    }


}
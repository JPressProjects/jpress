package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class MenuServiceProvider extends JbootServiceBase<Menu> implements MenuService {

    @Override
    public int sync(List<Menu> menus) {

        if (menus == null || menus.isEmpty()) {
            return 0;
        }

        int syncCounter = 0;
        for (Menu menu : menus) {

            Menu dbMenu = DAO.findFirstByColumn("url", menu.getUrl());
            
            if (dbMenu == null) {
                menu.save();
                syncCounter++;
            }
        }
        return syncCounter;
    }
}
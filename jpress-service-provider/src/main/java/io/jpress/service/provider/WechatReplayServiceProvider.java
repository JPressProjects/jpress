package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.service.WechatReplayService;
import io.jpress.model.WechatReplay;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class WechatReplayServiceProvider extends JbootServiceBase<WechatReplay> implements WechatReplayService {


    @Override
    public Page<WechatReplay> paginate(int page, int pageSize) {
        return DAO.paginate(page, pageSize, "id desc");
    }
}
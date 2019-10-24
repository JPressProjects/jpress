package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserFavoriteService;
import io.jpress.model.UserFavorite;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserFavoriteServiceProvider extends JbootServiceBase<UserFavorite> implements UserFavoriteService {

}
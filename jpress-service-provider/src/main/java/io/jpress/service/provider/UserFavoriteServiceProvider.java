package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserFavoriteService;
import io.jpress.model.UserFavorite;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserFavoriteServiceProvider extends JbootServiceBase<UserFavorite> implements UserFavoriteService {

    @Override
    public Page<UserFavorite> paginateByUserIdAndType(int page, int pagesize, Long userId, String type) {
        return paginateByColumns(page,pagesize, Columns.create("user_id",userId).eq("type",type),"id desc");
    }
     /**
     * 是否收藏了
     * @param userId
     * @param type
     * @param id
     * @return
     */
    @Override
    public boolean isFav(Long userId, String type, Long id){
        return findCountByColumns(Columns.create("user_id",userId).eq("type",type).eq("type_id",id)) >0;
    }

    @Override
    public boolean isProductFav(Long userId, Long id){
        return isFav(userId,"product",id);
    }
    @Override
    public boolean isArticleFav(Long userId, Long id){
        return isFav(userId,"article",id);
    }
}
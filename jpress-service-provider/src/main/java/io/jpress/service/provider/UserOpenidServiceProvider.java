package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;

import java.util.List;

@Bean
public class UserOpenidServiceProvider extends JPressServiceBase<UserOpenid> implements UserOpenidService {

    @Inject
    private UserService userService;


    @Override
    @Cacheable(name = "useropenid",key = "#(type)-#(openId)")
    public User findByTypeAndOpenId(String type, String openId) {
        if (StrUtil.isBlank(type) || StrUtil.isBlank(openId)){
            return null;
        }
        UserOpenid userOpenid = DAO.findFirstByColumns(Columns.create("type", type).eq("value", openId));
        return userOpenid != null ? userService.findById(userOpenid.getUserId()) : null;
    }

    @Override
    public boolean saveOrUpdate(Object userId, String type, String openId) {

        if (StrUtil.isBlank(openId)){
            return false;
        }

        UserOpenid userOpenid = findByUserIdAndType(userId, type);
        if (userOpenid != null) {
            String oldOpenId = userOpenid.getValue();
            if (!openId.equals(oldOpenId)) {
                userOpenid.setValue(openId);
                update(userOpenid);
            }
            return true;
        }


        userOpenid = new UserOpenid();
        userOpenid.setValue(openId);
        userOpenid.setType(type);
        userOpenid.setUserId((Long) userId);

        save(userOpenid);

        return true;
    }

    @Override
    public void shouldUpdateCache(int action, Model model, Object id) {
       if (model instanceof UserOpenid){
           UserOpenid userOpenid = (UserOpenid) model;
           Jboot.getCache().remove("useropenid",userOpenid.getType()+"-"+userOpenid.getValue());
       }
    }

    @Override
    public UserOpenid findByUserIdAndType(Object userId, String type) {
        List<UserOpenid> userOpenids = findListByUserId(userId);
        if (userOpenids == null){
            return null;
        }

        for (UserOpenid userOpenid : userOpenids){
            if (type.equals(userOpenid.getType())){
                return userOpenid;
            }
        }

        return null;
    }

    @Override
    @CacheEvict(name = "useropenid")
    public void batchDeleteByUserId(Object userId) {
        DAO.deleteByColumn(Column.create("user_id", userId));
    }


    @Override
    public List<UserOpenid> findListByUserId(Object userId) {
        return DAO.findListByColumn(Column.create("user_id", userId));
    }
}
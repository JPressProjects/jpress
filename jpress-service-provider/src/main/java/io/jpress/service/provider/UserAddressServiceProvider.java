package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ArrayUtil;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.UserAddress;
import io.jpress.service.UserAddressService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Bean
public class UserAddressServiceProvider extends JbootServiceBase<UserAddress> implements UserAddressService {

    @Override
    public Page<UserAddress> paginate(int page, int pageSize, Long userId) {
        return paginateByColumns(page, pageSize, Columns.create("user_id", userId), "id desc");
    }

    @Override
    public UserAddress findDefaultAddress(long userId) {
        List<UserAddress> userAddresses = findListByUserId(userId);
        if (userAddresses == null || userAddresses.isEmpty()) {
            return null;
        }
        Optional<UserAddress> userAddressOptional = userAddresses.stream().filter(userAddress -> userAddress.isDefault()).findFirst();
        return userAddressOptional.isPresent() ? userAddressOptional.get() : null;
    }

    @Override
    public List<UserAddress> findListByUserId(long userId) {
        return DAO.findListByColumn(Column.create("user_id", userId));
    }

    @Override
    public void addUserAddress(UserAddress address, long userid){
        if (address==null){
            return;
        }
        address.setUserId(userid);
        if (address.getId() != null) {
            address.setModified(new Date());
        }
        CommonsUtils.escapeModel(address);//xss safe
        Long addressId = (Long) saveOrUpdate(address);
        //新设置了默认，那么其他地址改为非默认
        if (address.getWidthDefault()) {
            Columns columns = Columns.create();
            columns.add("user_id", userid);
            columns.eq("width_default", true);
            List<UserAddress> list = findListByColumns(columns);
            if (list != null && list.size() > 0) {
                for (UserAddress userAddress : list) {
                    userAddress.setWidthDefault(userAddress.getId()==addressId);
                    userAddress.update();
                }
            }
        }

    }
}

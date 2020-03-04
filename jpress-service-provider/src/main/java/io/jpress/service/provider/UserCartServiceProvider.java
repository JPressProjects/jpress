package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.core.finance.ProductManager;
import io.jpress.model.UserCart;
import io.jpress.service.MemberPriceService;
import io.jpress.service.UserCartService;

import java.math.BigDecimal;
import java.util.List;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {

    @Inject
    private MemberPriceService memberPriceService;

    @Override
    public UserCart findById(Object id) {
        UserCart userCart = super.findById(id);
        return joinMemberPrice(userCart);
    }

    @Override
    public Object save(UserCart model) {
        UserCart userCart = findByProductInfo(model.getUserId(), model.getProductType(), model.getProductId(), model.getProductSpec());
        if (userCart == null) {
            return super.save(model);
        } else {
            userCart.setProductCount(userCart.getProductCount() + model.getProductCount());
        }
        update(userCart);
        return userCart.getId();
    }

    @Override
    @Cacheable(name = "usercarts", key = "#(userId)")
    public List<UserCart> findListByUserId(Object userId) {
        List<UserCart> userCarts = DAO.findListByColumns(Columns.create("user_id", userId), "id desc", 10);
        return joinMemberPrice(userCarts);
    }

    @Override
    @Cacheable(name = "usercartscount", key = "#(userId)")
    public long findCountByUserId(Object userId) {
        return DAO.findCountByColumn(Column.create("user_id", userId));
    }

    @Override
    public List<UserCart> findSelectedListByUserId(Long userId) {
        List<UserCart> userCarts = DAO.findListByColumns(Columns.create("user_id", userId).eq("selected", true), "id desc");
        return joinMemberPrice(userCarts);
    }

    @Override
    public UserCart findByProductInfo(long userId, String productType, long productId, String productSpec) {
        Columns columns = Columns.create("user_id", userId)
                .eq("product_type", productType)
                .eq("product_id", productId)
                .eq("product_spec", productSpec);

        UserCart userCart = DAO.findFirstByColumns(columns);
        return joinMemberPrice(userCart);
    }

    @Override
    public Page<UserCart> paginateByUser(int page, int pageSize, Long userId) {
        Page<UserCart> userCartPage = paginateByColumns(page, pageSize, Columns.create("user_id", userId), "id desc");
        joinMemberPrice(userCartPage.getList());
        return userCartPage;
    }

    @Override
    public long querySelectedCount(Long userId) {
        return DAO.findCountByColumns(Columns.create("user_id", userId).eq("selected", true));
    }


    private List<UserCart> joinMemberPrice(List<UserCart> userCarts) {
        if (userCarts != null) {
            userCarts.forEach(this::joinMemberPrice);
        }
        return userCarts;
    }

    private UserCart joinMemberPrice(UserCart userCart) {
        if (userCart == null) {
            return null;
        }

        // 会员价
        BigDecimal memberPrice = memberPriceService.queryPrice(userCart.getProductType(), userCart.getProductId(), userCart.getUserId());
        if (memberPrice != null) {
            userCart.put("memberPrice", memberPrice);
        }

        // 产品的最新价格 （用户添加商品到购物车后，商品的价格可能会发生变化）
        BigDecimal newestSalePrice = ProductManager.me().querySalePrice(
                userCart
                , userCart.getProductId()
                , userCart.getProductSpec()
                , userCart.getUserId());

        if (newestSalePrice != null) {
            userCart.put("newestSalePrice", newestSalePrice);
        }

        return userCart;
    }

    @Override
    @CachesEvict({
            @CacheEvict(name = "usercarts", key = "#(model.user_id)", unless = "model == null"),
            @CacheEvict(name = "usercartscount", key = "#(model.user_id)", unless = "model == null"),
    })
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }
}
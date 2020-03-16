package io.jpress.core.finance;

import io.jpress.model.UserCart;
import io.jpress.model.UserOrderItem;

import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/14
 */
public interface ProductOptionsRender {

    public Map doRenderUserCartOptions(UserCart userCart);

    public Map doRenderUserCartOptions(UserOrderItem userOrderItem);
}

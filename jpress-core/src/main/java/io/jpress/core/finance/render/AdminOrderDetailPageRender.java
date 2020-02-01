package io.jpress.core.finance.render;

import com.jfinal.core.Controller;
import io.jpress.model.UserOrder;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/1
 */
public interface AdminOrderDetailPageRender {

    public void doRender(Controller controller, UserOrder userOrder);

}

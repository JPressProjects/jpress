package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserOrderInvoiceService;
import io.jpress.model.UserOrderInvoice;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderInvoiceServiceProvider extends JbootServiceBase<UserOrderInvoice> implements UserOrderInvoiceService {

}
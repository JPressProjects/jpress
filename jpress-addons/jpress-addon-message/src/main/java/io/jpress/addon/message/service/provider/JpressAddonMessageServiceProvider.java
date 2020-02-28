package io.jpress.addon.message.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.addon.message.service.JpressAddonMessageService;
import io.jpress.addon.message.model.JpressAddonMessage;
import io.jboot.service.JbootServiceBase;

@Bean
public class JpressAddonMessageServiceProvider extends JbootServiceBase<JpressAddonMessage> implements JpressAddonMessageService {

}
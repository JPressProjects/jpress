package io.jpress.web.api;

import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.test.MockMethod;
import io.jboot.test.MockMvc;
import io.jboot.test.TestConfig;
import io.jboot.test.junit4.JbootRunner;
import io.jpress.JPressCoreInitializer;
import io.jpress.commons.url.FlatUrlHandler;
import io.jpress.core.addon.controller.AddonControllerProcesser;
import io.jpress.core.addon.handler.AddonHandlerProcesser;
import io.jpress.model.Utm;
import io.jpress.service.UtmService;
import io.jpress.web.WebInitializer;
import io.jpress.web.handler.JPressHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(JbootRunner.class)
@TestConfig(autoMockInterface = true)
public class OptionApiControllerTest {

    private static final MockMvc mvc = new MockMvc();

    @Test
    public void query() {
        mvc.get("/api/option/query?key=myKey").printResult();
    }

    @Test
    public void set() {
    }

    @MockMethod(targetClass = WebInitializer.class,targetMethod = "onEngineConfig")
    public void mockOnEngineConfig(Engine engine){
        System.out.println(">>>>>>>>>onEngineConfig: " + engine);
    }


    @MockMethod(targetClass = JPressCoreInitializer.class)
    public void onHandlerConfig(JfinalHandlers handlers){
        System.out.println(">>>>>>>>>JfinalHandlers: " + handlers);
        handlers.add(new JPressHandler());
        handlers.add(new FlatUrlHandler());
        handlers.add(new AddonHandlerProcesser());

        handlers.setActionHandler(new AddonControllerProcesser());
    }

    @MockMethod(targetClass = UtmService.class)
    public void doRecord(Utm utm){
        System.out.println(">>>>>>>>>doRecord: " + utm);
    }
}
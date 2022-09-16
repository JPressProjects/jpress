package io.jpress.web.api;

import com.jfinal.template.Engine;
import io.jboot.test.MockMethod;
import io.jboot.test.MockMvc;
import io.jboot.test.TestConfig;
import io.jboot.test.junit4.JbootRunner;
import io.jboot.web.handler.JbootActionReporter;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.web.WebInitializer;
import io.jpress.web.interceptor.ApiInterceptor;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JbootRunner.class)
@TestConfig(autoMockInterface = true, devMode = false)
public class BaseApiControllerTest {

    protected static final MockMvc mvc = new MockMvc();


    @Before
    public void test_init_before() {
        JPressOptions.set(JPressConsts.OPTION_API_ENABLE, Boolean.TRUE.toString());
        JPressOptions.set(JPressConsts.OPTION_API_APPID, "myAppId");
        JPressOptions.set(JPressConsts.OPTION_API_SECRET, "mySecret");

        JbootActionReporter.setReportEnable(true);

        mvc.setRequestStartListener(request -> {
            request.addQueryParameter("jpressAppId", "myAppId");
            request.addQueryParameter("ct", String.valueOf(System.currentTimeMillis()));
            request.addQueryParameter("sign", ApiInterceptor.createLocalSign(request,"mySecret"));
        });
    }


    @MockMethod(targetClass = WebInitializer.class, targetMethod = "onEngineConfig")
    public void mockOnEngineConfig(Engine engine) {
    }


//    @MockMethod(targetClass = JPressCoreInitializer.class)
//    public void onHandlerConfig(JfinalHandlers handlers) {
//        handlers.add(new JPressHandler());
//    }
//
//    @MockMethod(targetClass = UtmService.class)
//    public void doRecord(Utm utm) {
//    }
}
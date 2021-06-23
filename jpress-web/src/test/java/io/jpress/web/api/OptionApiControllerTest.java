package io.jpress.web.api;

import com.jfinal.kit.JsonKit;
import io.jpress.JPressOptions;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * test {{@link OptionApiController}}
 */
public class OptionApiControllerTest extends BaseApiControllerTest {


    @Test
    public void test_option_query_key() {
        JPressOptions.set("myKey", "myValue");
        mvc.get("/api/option/query?key=myKey").printResult()
                .assertJson(jsonObject -> Assert.assertEquals(jsonObject.get("value"), "myValue"));
    }


    @Test
    public void test_option_query_multi_key() {
        JPressOptions.set("myKey1", "myValue1");
        JPressOptions.set("myKey2", "myValue2");
        mvc.get("/api/option/query?key=myKey1,myKey2").printResult();
    }


    @Test
    public void test_option_set() {
        Map<String, String> keyAndValues = new HashMap<>();
        keyAndValues.put("myKey1", "myValue1");
        keyAndValues.put("myKey2", "myValue2");

        String jsonContent = JsonKit.toJson(keyAndValues);
        mvc.post("/api/option/set", jsonContent).printContent();


        Assert.assertEquals(JPressOptions.get("myKey1"), "myValue1");
        Assert.assertEquals(JPressOptions.get("myKey2"), "myValue2");
    }


}
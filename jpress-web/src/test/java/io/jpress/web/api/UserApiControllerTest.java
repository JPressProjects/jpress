package io.jpress.web.api;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import io.jboot.support.jwt.JwtManager;
import io.jboot.test.MockMethod;
import io.jpress.JPressOptions;
import io.jpress.model.User;
import io.jpress.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
/**
 * test {{@link UserApiController}}
 */
public class UserApiControllerTest extends BaseApiControllerTest {


    @Test
    public void test_user_login() {
        JwtManager.getConfig().setSecret("mySecret");

        Map<String,Object> paras = new HashMap<>();
        paras.put("loginAccount","user001");
        paras.put("password","123456");

        mvc.post("/api/user/login",paras).printResult();
    }


    @MockMethod(targetClass = UserService.class,targetMethod = "findByUsernameOrEmail")
    public User mock_findByUsernameOrEmail(String account){
        return new User();
    }


    @MockMethod(targetClass = UserService.class,targetMethod = "doValidateUserPwd")
    public Ret mock_doValidateUserPwd(User user, String pwd){
        return Ret.ok().set("user_id",123456);
    }


    @Test
    public void test_user_query() {
        mvc.get("/api/user/query?id=1").printResult();
    }


    @MockMethod(targetClass = UserService.class,targetMethod = "findById")
    public User mock_findById(Object id){
        User user =  new User();
        user.setId(1L);
        return user;
    }



    @Test
    public void test_user_update() {
        JPressOptions.set("myKey1", "myValue1");
        JPressOptions.set("myKey2", "myValue2");
        mvc.get("/api/option/query?key=myKey1,myKey2").printResult();
    }


    @Test
    public void test_user_create() {
        Map<String, String> keyAndValues = new HashMap<>();
        keyAndValues.put("myKey1", "myValue1");
        keyAndValues.put("myKey2", "myValue2");

        String jsonContent = JsonKit.toJson(keyAndValues);
        mvc.post("/api/option/set", jsonContent).printContent();


        Assert.assertEquals(JPressOptions.get("myKey1"), "myValue1");
        Assert.assertEquals(JPressOptions.get("myKey2"), "myValue2");
    }
}
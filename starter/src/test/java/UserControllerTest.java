import io.jboot.test.MockMvc;
import io.jboot.test.junit4.JbootRunner;
import io.jboot.web.handler.JbootActionReporter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JbootRunner.class)
public class UserControllerTest {

    private static MockMvc mvc = new MockMvc();

    @BeforeClass
    public static void init(){
        System.out.println(">>>>>>>>BeforeClass.init");
        JbootActionReporter.setReportEnable(true);
    }

    @Test
    public void test_login(){
        mvc.get("/user/login").printResult();
    }
}

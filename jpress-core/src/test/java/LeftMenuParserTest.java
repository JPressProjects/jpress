import com.jfinal.kit.PathKit;
import io.jpress.admin.LeftMenu;
import io.jpress.admin.LeftMenuParser;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package PACKAGE_NAME
 */
public class LeftMenuParserTest {


    @Test
    public void testParser() {
        File file = new File(PathKit.getWebRootPath(), "/src/test/resources/testmenu.txt");
        System.out.println(file.getAbsolutePath());
        LeftMenu leftMenu = LeftMenuParser.parseMenu(file);
        System.out.println(Arrays.toString(leftMenu.getItems().toArray()));
    }
}

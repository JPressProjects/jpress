# JPress 代码生成器的使用



在开发的时候，我们可以在 jpress 项目的 starter 模块下，建立一个新的代码生成器java类，用于对模块的代码生成，也可以参考 starter 模块，自己编写一个属于自己项目的 starter。

代码生成器如下：

```java
public class PageModuleGenerator {


    private static String dbUrl = "jdbc:mysql://127.0.0.1:3306/jpressdb";
    private static String dbUser = "root";
    private static String dbPassword = "";


    private static String moduleName = "club"; 
    private static String dbTables = "club_category,club_post,club_post_comment";
    private static String modelPackage = "io.jpress.module.club.model";
    private static String servicePackage = "io.jpress.module.club.service";

    public static void main(String[] args) {

        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleName, dbUrl, dbUser, dbPassword, dbTables, modelPackage, servicePackage);
        moduleGenerator.gen();

    }
}
```

执行完 `main()` 方法后，会在当前目录下生产一个叫 club 的新的maven模块。
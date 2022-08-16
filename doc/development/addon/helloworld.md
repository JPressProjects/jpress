# JPress 的 Helle World 插件



在学习 JPress 插件的 hello world 之前，建议你把 JPress 的项目已经下载到了你本地，通过此文档在对比你本地的 helloworld 插件项目，会有更好的理解。

倘若你不方便下载，也可以通过这个url进行了解：<a href="https://gitee.com/JPressProjects/jpress/tree/master/jpress-addons/jpress-addon-helloworld" target="_blank">https://gitee.com/JPressProjects/jpress/tree/master/jpress-addons/jpress-addon-helloworld</a>

首先，一个最基本的 插件目录结构，如下：

```
jpress-addon-helloworld
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── io.jpress.addon.helloworld
    │   │        ├── HelloWorldAddonController.java
    │   │        ├── xxx.java（此章节忽略）
    │   ├── resources
    │   │   ├── addon.txt
    │   │   └── config.txt
    │   └── webapp
    │       └── helloworld
    │           └── index.html
```
这个目录结构，和一个普通的 Java maven 项目没什么区别，因为它就是一个普通的Maven项目（或者是maven module）。

唯一的区别的部分是在项目的 resources 目录下有一个 `addon.txt` 和 `config.txt` 文件（`config.txt`不是必须的），假设一行代码都不写，只要有` addon.txt` ，只需要我们把此maven模块编译成jar包后，就可以在jpress后台进行安装了，他已经是一个jpress插件。


接下来，我们来看下 **HelloWroldAddonController.java** 这个类，代码如下：

```java
@RequestMapping(value = "/helloworld",viewPath = "/")
public class HelloWorldAddonController extends JbootController {

    public void index() {
        setAttr("version","1.0.2");
        render("helloworld/index.html");
    }

    public void json() {
        renderJson(Ret.ok().set("message", "json ok...."));
    }

    @ActionKey("/admin/addon/test")
    @AdminMenu(groupId = JPressConsts.SYSTEM_MENU_ADDON, text = "插件测试")
    public void adminmenutest() {
        renderText("addon test");
    }
}
```

此时，当我们在JPress的后台安装此插件，并启动之后，就可以通过 `http://127.0.0.1:8080/helloworld` 访问到此插件的 `HelloWorldAddonController.index()` 方法，并正常渲染 hellowrold/index.html 的内容。

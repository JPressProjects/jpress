# JPress 插件安装

上 [《hello world》](./helloworld.md) 章节中，我们已经了解到，一个最基本的 JPress 插件项目。

但是，插件本身可能有自己的 Model ，自己的表结构、自己的 html 、css、js、图片等资源文件。JPress 在安装的时候，会自动释放插件资源到磁盘上来，同时
会调用插件所有实现 `Addon` 接口的类。





一个完整的 hello world 项目的目录结构应该如下：

```
jpress-addon-helloworld
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── io.jpress.addon.helloworld
    │   │        ├── HelloWorldAddon.java
    │   │        └── HelloWorldAddonController.java
    │   ├── resources
    │   │   ├── addon.txt
    │   │   └── config.txt
    │   └── webapp
    │       └── helloworld
    │           └── index.html
```

在源码中，可以看到有 `HelloWorldAddon` 这个类，其作用是用于监听本插件在安装、卸载、启用和停止的动作，然后做对于的改变。 其代码如下：


```java
public class HelloWorldAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {}

    @Override
    public void onUninstall(AddonInfo addonInfo) {}

    @Override
    public void onStart(AddonInfo addonInfo) {}

    @Override
    public void onStop(AddonInfo addonInfo) {}
}
```

- `onInstall()` ： 用于在此插件被安装的时候执行，在这个插件的生命周期中只会执行一次，就是被安装的时候，在这个方法中，我们往往在这里创建需要的数据库表等。
- `onUninstall()`：用于在此插件被卸载的时候执行，这这个操作的往往是和 `onInstall()` 相反的，当 `onInstall()` 创建表或其他资源，我们应该在 `onUninstall()` 删除表或删除 `onInstall(`) 创建的资源。
- `onStart()`：此方法是网站管理员在后台进行启动的时候触发的，当次插件被启动之后，以后只要重启容器比如tomcat、undertow等都会执行此方法。在此方法中，我们可以用来创建自己后台菜单、用户中心菜单等。
- `onStop()`：和 `onStart()` 相反，比如在 `onStart()` 创建了菜单，那么在 `onStop()` 就应该把菜单给移除。


**备注：**

>HelloWorldAddon 不是必须的，当其不存在的时候，JPress 在安装、卸载都不会执行该插件的任何方法。




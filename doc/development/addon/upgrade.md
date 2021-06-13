# JPress 插件升级

JPress 支持插件动态升级的功能，比如，我们在 JPress 后台安装了某个插件，但是后来发现这个插件存在 bug，需要修复。
此时，我们修改升级 `addon.txt` 配置文件里的版本号，重新打包插件。即可在 JPress 后台对该插件进行升级操作。

但是，在升级的过程中，我们可能会遇到很多情况，比如：

- 新版本插件的表结构发生了改变
- 旧版本可能生成了很多垃圾文件需要清空
- 等等

所以，JPress 提供了 `AddonUpgrader` 接口，只需要我们的 `新插件` 里存在此接口的实现类，当插件在升级的时候，就会自动调用此接口对应的方法。

```
jpress-addon-helloworld
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── io.jpress.addon.helloworld
    │   │        ├── HelloWorldAddon.java
    │   │        ├── HelloWorldAddonController.java
    │   │        └── HelloWorldUpgrader.java
    │   ├── resources
    │   │   ├── addon.txt
    │   │   └── config.txt
    │   └── webapp
    │       └── helloworld
    │           └── index.html
    └── test
```

在源码中，可以看到有 `HelloWorldUpgrader` 这个类，起作用是监听本插件在升级的过程中进行操作。

代码如下：

```java
public class HelloWorldUpgrader implements AddonUpgrader {
    @Override
    public boolean onUpgrade(AddonInfo oldAddon, AddonInfo thisAddon) {
        
    }

    @Override
    public void onRollback(AddonInfo oldAddon, AddonInfo thisAddon) {
        
    }
}
```

- `onUpgrade()` : 当用户在后台对插件进行升级的时候，JPress 会调用新插件的`HelloWorldUpgrader.onUpgrade()` 方法，在此方法中，我们可以对修插件的表结构进行修改等操作。
- `onRollback()`：当J Press 调用新插件的 `onUpgrade()` 的时候出现了异常、或者此方法返回 `false`，证明此插件升级失败，JPress 会对整个插件进行回退到上一个版本，并会调用新插件的 `onRollback()` 方法。

**备注：**

> HelloWorldAddonUpgrader 不是必须的，不存在的时候，JPress 在升级的时候不会执行该插件的任何方法。




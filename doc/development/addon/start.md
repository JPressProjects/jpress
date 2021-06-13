# JPress 插件简介

插件是 JPress 对其他功能扩展的一种方式，在 JPress 中，编写插件不需要学习任何的 JPress 内置代码和“钩子（hook）”，因为在 JPress 中，并不像其他应用一样提供了“钩子（hook）”的功能。

JPress 的插件，与其说是插件，不如说是应用可能更好的理解，因为在JPress插件中，你可以在插件里编写控制器（Controller）、model、拦截器（Interceptor）、Handler...等等，就像一个JFinal的应用一模一样。

JPress 插件和应用不一样的地方，只是在项目的resource下增加了一个插件的配置 addon.txt 文件，用于对此应用（或者说是插件）的描述。例如：

```properties
id=io.jpress.addon.club
title=JPress 社区
description=JPress 官方开发的交流社区
author=海哥
authorWebsite=
version=1.0.2
versionCode=3
```

他们分别是
- id：插件的唯一ID，在一个JPress应用中只能安装同一个ID的插件。建议用包名+项目名的方式进行命名
- title：插件的标题或名称
- description：插件的描述
- author：插件的作者
- authroWebsite：插件作者的官方网站
- version：版本
- versionCode：版本号，当该插件进行升级的时候，用对对比当前插件是否可以升级


在编写插件的时候，以上配置文件 addon.txt 必须被打包的jar包中才能进行安装， 所以在此插件的 Maven module 的 pom.xml 中，比如添加如下的配置：

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.*</include>
            </includes>
            <filtering>false</filtering>
        </resource>
    </resources>    
</build>
```
以保障，在maven编译的时候正常把 addon.txt 文件打包到插件的jar中去。

倘若，此插件还有html、css等文件存放在 webapp 目录下，完整的 resources 应该如下配置：

```xml
<build>

    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.*</include>
            </includes>
            <filtering>false</filtering>
        </resource>
        
        <resource>
            <directory>src/main/webapp</directory>
            <includes>
                <include>**/*.*</include>
            </includes>
            <filtering>false</filtering>
        </resource>
    </resources>

</build>
```

可以参考插件的 hello world 项目：<a href="https://gitee.com/JPressProjects/jpress/blob/master/jpress-addons/jpress-addon-helloworld/pom.xml" target="_blank">https://gitee.com/JPressProjects/jpress/blob/master/jpress-addons/jpress-addon-helloworld/pom.xml</a>




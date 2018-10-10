# JPress 文档

## 目录


## 安装部署

在安装JPress之前，你应该对数据库、服务器、Java、Maven等有基本的认识和了解。本文档是建立在这些基础知识之上的。

在安装JPress之前，需要你在你自己的电脑（或服务器）安装好Mysql数据库、Java环境 和 Maven编译环境，如果不会安装，可以通过 `oneinstack` 等第三方一键安装好 java、mysql 和 nginx 环境。

另外可以通过以下几种方式获得帮助：

1. JPress官方公众号：jpressio
2. JPress交流QQ群：
3. 开源中国进行发帖提问。

### JPress安装

JPress安装需要以下几个步骤：

1. 下载JPress源码
2. 通过maven编译JPress成war包（或可执行程序）
3. 创建JPress数据库
4. 修改jpress数据库链接配置并启动

#### 1.下载JPress源码

下载JPress源码通过以下几种方式：

1、git clone

```
git clone https://gitee.com/fuhai/jpress.git
```

2、进入 Gitee 的 JPress 发行页面进行下载

链接地址： https://gitee.com/fuhai/jpress/releases

#### 2.通过Maven编译JPress成war包和可执行程序

JPress可以编译成war包和可执行程序，war需要在tomcat等web容器下运行。可执行程序内置undertow，不需要其他第三方web容器，运行脚本即可通过浏览器访问jpress应用。


下载好 JPress 源码后，通过 shell 进入源码目录，执行如下 maven 命令：

```shell
mvn package
```

稍等片刻，待命令执行完毕之后，即可在 `starter-tomcat/target` 目录下生成 `starter-tomcat-1.0.war` 的war包，在 `starter/target/generated-resources/appassembler/jsw/` 目录下生成 jpress 的文件夹，jpress 文件夹的目录如下：

```
├── bin
│   ├── jpress
│   ├── jpress.bat
│   ├── wrapper-linux-x86-32
│   ├── wrapper-linux-x86-64
│   ├── wrapper-macosx-universal-32
│   ├── wrapper-macosx-universal-64
│   ├── wrapper-windows-x86-32.exe
│   └── wrapper-windows-x86-64.exe
├── lib
├── logs
├── tmp
└── webRoot
    ├── jboot.properties
    ├── logback.xml
    └── wrapper.conf
```

 若编译不通过注意事项：
 
 * maven版本建议用3.0 以上，2.x没有测试过
 * java版本1.8
 * maven注意添加aliyun的源，修改 `maven/conf/setting.xml`文件，找到 mirrors 节点 ，修改如下：

 ```xml
<mirrors>
        <mirror>  
        	  <id>alimaven</id>  
        	  <name>aliyun maven</name>  
        	  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>  
        	  <mirrorOf>central</mirrorOf>  
    	</mirror>
</mirrors>

 ```


#### 3.创建JPress数据库

通过Mysql客户端连接Mysql数据库后，通过运行 JPress 目录下的 db.sql，既可创建 JPress 相关数据库。


 
#### 4.修改jpress数据库链接配置并启动

##### 启动 jpress war 包

拷贝`starter-tomcat/target` 目录下的 `starter-tomcat-1.0.war` war包，放到tomcat的webapp目录下，手动解压缩。

找到 WEB-INF/classes/jboot.properties 文件，并配置如下：

```
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/数据库名称
jboot.datasource.user=数据库账号
jboot.datasource.password=数据库密码
```

配置成功后，启动tomcat（运行 `tomcat/bin/startup.sh`），浏览器输入 `http://127.0.0.1:8080/starter-tomcat-1.0` 即可访问。若把 `tomcat/webapp/starter-tomcat-1.0` 里面的文件拷贝到 `tomcat/webapp/ROOT`，访问`http://127.0.0.1:8080`即可。


##### 启动 jpress 可执行程序


拷贝`starter/target/generated-resources/appassembler/jsw/` 的 `jpress` 目录，放到 Linux 上。 

修改 `webRoot/jboot.properties` 配置文件数据库连接：

```
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/数据库名称
jboot.datasource.user=数据库账号
jboot.datasource.password=数据库密码
```


执行 `./bin/jpress` 脚本也可以启动jpress项目（window系统下执行 `./bin/jpress.bat` ）。

需要注意的是，在 Linux 下，需要给与 `jpress`，`wrapper-linux-x86-32` 和 `wrapper-linux-x86-64` 可执行权限。


## 模板制作

### 模板概述
### 首页模板
### 列表页模板
### 详情页模板

## 二次开发

## 后台使用


#### JPress模板制作教程

##### 描述
  
  JPress模板是由一些列 html、css、js 和 JPress 标签组成的文件目录。
  
  JPress 目录结构如下：
  
  * index.html 首页模板
  * error.html 错误页面模板
  * setting.html 模板设置页面
  * page.html 页面默认模板
  * article.html 文章详情的默认模板
  * artlist.html 文章类别的默认模板
  * template.properties 模板信息配置
  * screenshot.png 缩略图
  
  其中，template.properties 文件配置如下
  
 ```java
id= cn.jeanstudio.bluelight
title= BlueLight
description= BlueLight是JeanStudio工作室为JPress设计的官网模板
anthor= jeanStudio
authorWebsite= http://www.jeanstudio.cn
version=1.0
versionCode=1
updateUrl=
screenshot=screenshot.png
```

* id ：模板ID，全网唯一，建议用域名+名称的命名方式
* title ：模板名称
* description ：模板简介
* anthor ：模板作者
* authorWebsite ：模板作者的官网
* version ：版本
* versionCode ：版本号
* updateUrl ：此模板升级的url地址
* screenshot ：此模板的缩略图图片（不填写默认为：screenshot.png）


##### 模板标签

JPress是基于JFinal和Jboot进行开发的，使用的模板引擎是Jfinal Enjoy，Jfinal Enjoy 拥有极好的性能和开发体验，在学习JPress模板制作之前，很有必要学习一下Jfinal Enjoy 模板引擎。
 

 
使用代码如下：
    
```html
#for(menu : MENUS)
    <li class="nav-item">
        <a href="#(menu.url ??)">#(menu.text ??)</a>
    </li>
#end
```

* 文章分类标签：#articlePage()
  
使用代码如下：
  
```html
#articlePage()

    #for(article : articlePage.list)
        <a href="#(article.url ??)">#(article.title ??)</a>
    #end
    
    #articlePaginate()
            #for(page : pages)
                <a href="#(page.url ??)">#(page.text ??)</a>
            #end
     #end
#end
```



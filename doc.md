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

JPress模板主要是由html、css、js和JPress标签组成，JPress标签的主要作用是用于读取后台数据，逻辑控制。

与此同时、JPress模板文件的文件名也是固定的，目录结构如下：


|模板文件| 描述 | 备注 |
| --- | --- | --- |
| index.html |首页模板|  |
| error.html |错误页面模板| 当系统发生错误的时候，会自动调用此页面进行渲染，也可以扩展为 error_404.html，当发生404错误的时候优先使用此文件，同理可以扩展 error_500.html ，当系统发生500错误的时候调用此文件渲染。 |
| setting.html |后台的模板设置页面| 当次html不存在的时候，用户进入后台的模板设置，会显示此模板不支持设置功能 |
| screenshot.png |模板缩略图| 用于在后台的模板列表里显示的图片  |
| template.properties |模板信息描述文件|  文件格式在下方 |
| page.html |页面模块的模板|  page.html 可以扩展为 page_aaa.html 、page_bbbb.html ，当模板扩展出 page_xxx.html 的时候，用户在后台发布页面内容的时候，就可以选择使用哪个模板样式进行渲染。例如： page_xxx.html 其中 `xxx` 为样式的名称。|
| article.html | 文章详情模板| 和page模块一样，article.html 可以扩展出 article_styel1.html、article_style2.html，这样，用户在后台发布文章的时候，可以选择文章样式。（备注：用户中心投稿不能选择样式）  |
| artlist.html | 文章列表模板| 和page、article一样，可以通过样式 |


备注：所有的模板文件都可以扩展出专门用于渲染手机的模板，例如：首页的渲染模板是 `index.html` ，如果当前目录下有 `index_h5.html`，那么，当网站用户通过手机浏览网站的时候，JPress 会自动使用 `index_h5.html` 去渲染。 page 和 article、artlist 同理。

template.properties 文件配置如下
  
```
id = cn.jeanstudio.bluelight
title = BlueLight
description = BlueLight是JeanStudio工作室为JPress设计的官网模板
anthor = jeanStudio
authorWebsite = http://www.jeanstudio.cn
version = 1.0
versionCode = 1
updateUrl =
screenshot = screenshot.png
```

* id ：模板ID，全网唯一，建议用域名+名称的命名方式
* title ：模板名称
* description ：模板简介
* anthor ：模板作者
* authorWebsite ：模板作者的官网
* version ：版本（不添加默认为1.0.0）
* versionCode ：版本号（只能是数字，不填写默认为1）
* updateUrl ：此模板升级的url地址
* screenshot ：此模板的缩略图图片（不填写默认为：screenshot.png）




#### 模板标签

有了以上这些目录结构，实际上不用任何的标签就可以成为一套模板了，只是这个模板是静态模板，不能读取后台数据。

只有通过在静态的html上，添加 JPress 标签，才能可以读取后台数据。

目前，JPress内置的模板标签如下：

**1、全局标签，全局标签用全用大写显示，全局标签在任意模板页面都可以使用。**

| 标签名称 | 数据类型 | 标签描述 |  
| --- | --- | --- | 
| #(WEB_NAME ??) | 字符串 |  网站名称 |  
| #(WEB_TITLE ??) |  字符串 | 网站标题 |  
| #(ATTR_WEB_SUBTITLE ??) |  字符串 | 网站副标题 | 
| #(ATTR_WEB_DOMAIN ??) |  字符串 | 网站域名 | 
| #(ATTR_WEB_COPYRIGHT ??) |  字符串 | 网站版权信息 | 
| #(ATTR_SEO_TITLE ??) |  字符串 | 网站SEO标题 | 
| #(ATTR_SEO_KEYWORDS ??) |  字符串 | 网站SEO关键字 | 
| #(ATTR_SEO_DESCRIPTION ??) |  字符串 | 网站SEO描述 | 
| MENUS  | 数据列表( list ) | 菜单数据 | 

标签描述，标签建议用 `#( 名称 ??)` 的方式来读取数据，而不是用 `#(名称)` 两个问号（??）的意思是如果 后台填写的名称为空格，那么就用 两个问号（??）之后的内容来显示。

例如： 
`#(WEB_NAME ??)` 表示优先使用 WEB_NAME 来显示，但是当 WEB_NAME 为空的时候，显示空数据（因为两个问好（??）之后的内容为空）。

`#(WEB_NAME ?? WEB_TITLE)` 表示优先使用 WEB_NAME 来显示，但是当 WEB_NAME 为空的时候，用 WEB_TITLE（网站标题） 来显示。

`#(ATTR_SEO_TITLE ?? WEB_TITLE +'-'+ ATTR_WEB_SUBTITLE)` 表示优先使用 ATTR_SEO_TITLE（SEO标题） 来显示，但是当 ATTR_SEO_TITLE 为空的时候，用 `WEB_TITLE - WEB_SUBTITLE` 来显示。

有了以上知识后，我们的 index.html 首页模板文件可以如下：

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>#(SEO_TITLE ?? (WEB_TITLE + '-' + WEB_SUBTITLE))</title>
    <meta name="keywords" content="#(SEO_KEYWORDS ??)">
    <meta name="description" content="#(SEO_DESCRIPTION ??)">
</head>
<body>
这是首页....
</body>
</html>
```

以上内容都只是针对 `字符串` 这种数据类型进行展示的，全局标签中还有一种数据类型叫 `数据列表( list )`，那么，怎么来显示数据列表呢？

这个时候`#( 名称 ??)`就不能正常显示了，需要用到一个新的标签：

```java
#for 
... 
#end
```

`#for ... #end` 标签也叫循环标签，意思是把列表循环显示出来。

对于 `MENUS` 这种数据类型为 `数据列表( list )` 的数据，`#for ... #end` 标签使用如下。

   
```html
#for(menu : MENUS)
    <li> <a href="#(menu.url ??)">#(menu.text ??)</a> </li>
#end
```

这样，若我们在后台创建了5个菜单，那么html会输出 5个 `<li> ... </li>`



**2、数据指令，数据指令一般情况下只能用于特有页面**

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #article() | 任意 | 用于读取特定的单篇文章 |  
| #articles() | 任意 | 用于读取文章列表，例如：热门文章文章、最新评论文章列表等等 | 
| #articlePage() | artlist.html | 用于对文章列表进行的内容和分页进行显示 | 
| #commentPage() | article.html | 用于对文章评论的内容和分页进行显示 | 
| #nextArticle() | article.html | 下一篇文章 | 
| #previousArticle() | article.html | 上一篇文章 | 
| #relevantArticles() | article.html | 相关文章列表，相同标签的的文章 |
| #categories() | 任意 | 读取文章模块的所有分类 |  
| #articleCategories() | 任意 | 用于读取某一篇文章的所属分类，例如：文章的标签、文章的分类等 |  










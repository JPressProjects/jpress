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


执行 `./bin/jpress start` 脚本也可以启动jpress项目（window系统下先执行  `./bin/jpress.bat install`， 再执行 `./bin/jpress.bat start`）。

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

文章相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #article() | 任意 | 用于读取特定的单篇文章 |  
| #articles() | 任意 | 用于读取文章列表，例如：热门文章文章、最新评论文章列表等等 | 
| #articlePage() | 文章列表：artlist.html | 用于对文章列表进行的内容和分页进行显示 | 
| #commentPage() | 文章详情：article.html | 用于对文章评论的内容和分页进行显示 | 
| #nextArticle() | 文章详情：article.html | 下一篇文章 | 
| #previousArticle() | 文章详情：article.html | 上一篇文章 | 
| #relevantArticles() | 文章详情：article.html | 相关文章列表，相同标签的的文章 |
| #categories() | 任意 | 读取文章模块的所有分类 |  
| #articleCategories() | 任意 | 用于读取某一篇文章的所属分类，例如：文章的标签、文章的分类等 |  

页面相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #page() | 任意 | 用于读取某个页面 |  
| #pages() | 任意 | 用于读取页面列表 | 
 
 
 用户相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- |  
| #users() | 暂不支持 | 用于读取页面列表 | 


##### #article() 指令的用法

此指令是用来读取一篇文章，网站的任意页面进行展示。

```java
#article()
<a href="#(article.url)">#(article.title)</a>
<div>#(article.content)</div>
#end
```

##### #articles() 指令的用法

此指令是在任何页面，用来读取文章列表。例如：最新文章、热门文章等

```java
#articles(flag="",hasThumbnail="",orderBy="",count="")
    #for(article : articles)
        <a href="#(article.url)">#(article.title)</a>
    #end
#end
```

**#articles() 指令支持的参数有：**

* flag：文章标识，这个是在编辑文章的时候自由填写。
* hasThumbnail：是否需要缩略图，值为 true 和 false。
* orderBy ：根据什么进行排序，目前支持的值有：order_number（用户自定义排序）、comment_count（文章的评论数量）、comment_time（文章的评论时间）、view_count（文章的访问量）、created（文章的创建时间）、modified（文章的修改时间）
* count ：要显示多少篇文章

##### #articlePage() 指令的用法
指令 #articlePage() 只能用在文章列表页，也就是 artlist.html 模板文件及其扩展文件。

```java
#articlePage()

    #for(article : articlePage.list)
        <a href="#(article.url ??)">
        文章标题是：#(article.title ??)
        </a> 
        <div>
            文章内容是：#maxLength(article.text,100)
        </div>    
    #end

    #articlePaginate()
        #for(page : pages)
            <a class="page-link" href="#(page.url ??)">
                #(page.text ??)
            </a>
        #end
    #end
    
#end
```
**说明**
指令 #articlePage() 内部又包含了另一个指令 #articlePaginate()，#articlePaginate()是用于显示上一页和下一下。

**指令 #articlePage() 的参数有：**

* pageSize ：可以用来指定当前页面有多少条数据，默认值是：10。也就是说 `#articlePage()` 等同于 `#articlePage(pageSize=10)`

**分页指令#articlePaginate()的参数有**

* previousClass ：上一页的样式
* nextClass ：下一页的样式
* activeClass ：当前页面的样式
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式）
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容（默认值为：上一页）
* nextText ：下一页按钮的文本内容（默认值为：下一页）

##### #commentPage()指令的用法

指令 #commentPage() 只能用在文章详情页，也就是 article.html 模板文件及其扩展文件。用于读取这篇文章的相关评论信息以及评论的分页功能。

```java
#commentPage()

    #for(comment : commentPage.list)
        <div>评论内容是：#(comment.content ??)</div>  
        <div>评论作者是：#(comment.authro ??)</div>  
    #end

    #commentPaginate()
        #for(page : pages)
            <a class="page-link" href="#(page.url ??)">
                #(page.text ??)
            </a>
        #end
    #end
    
#end
```
**说明**
和一样#articlePage()，#commentPage()指令  内部又包含了另一个指令 #commentPaginate()，#commentPaginate()是用于显示评论的上一页和下一下。

**指令 #commentPage() 的参数有：**

* pageSize ：可以用来指定当前文章详情，每页的评论条数是多少，默认值是：10。也就是说 `#commentPage()` 等同于 `#commentPage(pageSize=10)`

**分页指令#commentPaginate()的参数有**

* previousClass ：上一页的样式
* nextClass ：下一页的样式
* activeClass ：当前页面的样式
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式）
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容（默认值为：上一页）
* nextText ：下一页按钮的文本内容（默认值为：下一页）

##### #nextArticle() 指令的用法

指令 #nextArticle() 只能用于**文章详情页**，用于显示下一篇文章的相关信息或者内容。

```java
#nextArticle()
<a href="#(next.url)">标题是：#(next.title ??)</a>
#end
```

##### #previousArticle() 指令的用法

指令 #previousArticle() 只能用于**文章详情页**，用于显示上一篇文章的相关信息或者内容。

```java
#previousArticle()
<a href="#(previous.url)">标题是：#(previous.title ??)</a>
#end
```

## JPress二次开发

通过 jpress 来做二次开发，是非常简单容易的。 jpress 提供了基本的用户管理、权限管理、微信公众号对接、小程序对接等基本功能。

开发者只需要关系自己模块的业务逻辑就可以了。

假设我们要使用 jpress 来开发一个小型的论坛，如何做呢？

主要有以下几个步骤：

* 1、需求分析和建库建表
* 2、通过 JPress 直接生成 maven 模块和相关基础代码
* 3、通过 实现 ModuleListener 配置模块基本信息
* 4、通过 注解 @AdminMenu 和  @UcenterMenu配置后台和用户中心菜单
* 5、编码实现模块基本逻辑

**1、需求分析和建库建表**

我们假设论坛有三个表、分表时论坛版块、帖子和帖子回复，因为jpress已经有用户表了，所以不再需要用户表。

为了能够讲清楚如何使用jpress进行二次开发，我们故意把论坛的版块功能给简化了，论坛版块暂时不支持子版块功能。

表名分别为：`club_category`、`club_post`、`club_post_comment`。


**2、通过 JPress 直接生成 maven 模块和相关基础代码**

我们在 jpress 项目的 starter 模块下，建立一个新的代码生成器，用于对社区模块的代码生成。

代码生成器如下：

```java
public class PageModuleGenerator {


    private static String dbUrl = "jdbc:mysql://127.0.0.1:3306/newjpress";
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

**3、通过 实现 ModuleListener 配置模块基本信息**

我们自动生成的 `module-club-web` 模块里，建立一个 叫 `ClubModuleListener` 的类，实现`ModuleListener`接口。

代码如下：

```java
public class ClubModuleListener implements ModuleListener {

    @Override
    public String onRenderDashboardBox(Controller controller) {
        //在这里配置后台首页的相关模块
        //代码可以参考 ArticleModuleLisenter
        return null;
    }

    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {
        //这里配置后台菜单
        //代码参考 ArticleModuleLisenter
    }

    @Override
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus) {
        //这里配置用户中心菜单
        //代码参考 ArticleModuleLisenter
    }
}
```
以上提到的`ArticleModuleLisenter`代码在： https://gitee.com/fuhai/jpress/blob/master/module-article/module-article-web/src/main/java/io/jpress/module/article/ArticleModuleLisenter.java

**4、通过 @AdminMenu 和  @UcenterMenu 配置后台和用户中心菜单**

我们来为club这个模块添加一个新的后台菜单，在这个之前，我们先来建立一个共识：

* 1、菜单，肯定是一个可以访问的url地址。
* 2、既然是可以访问的url地址，那么这个url地址肯定会对应某个Controller的某个方法。

因此，某个菜单，其实就是Controller的某个方法。

对于这个论坛系统，我们希望后台菜单显示如下：

```
论坛管理
>>> 帖子列表
>>> 回帖管理
>>> 版块管理
```

那么，我们需要建立一个叫 `_ClubController` 的类（名字任意取，后台相关的Controller，建议用下划线（_）开头，这样可以和 jpress 统一。）

代码如下：

```java
@RequestMapping("/admin/club")
public class _ClubController extends AdminControllerBase {

    @AdminMenu(text = "帖子列表", groupId = "club")
    public void index() {
        render("club/post_list.html");
    }
    
    @AdminMenu(text = "回帖管理", groupId = "club")
    public void index() {
        render("club/post_comment_list.html");
    }

    @AdminMenu(text = "版块管理", groupId = "club")
    public void index() {
        render("club/category_list.html");
    }  
}
```

**注意：**

* `@AdminMenu`里的`groupId`的值必须是`ClubModuleListener`里配置的id。如果我们把 `groupId` 修改为 `groupId = "page"`，那么此菜单将会被添加到后台的页 `页面管理` 这个菜单下面。
* `_ClubController` 必须继承至 `AdminControllerBase`。
* `@RequestMapping("/admin/club")`里的值必须是 `/admin/`开头。


## 微信插件开发

在 jpress 里，做微信开发非常简单，直接看代码：

```java
public class HelloWechatAddon implements WechatAddon {

    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {

        return false;
    }

    @Override
    public boolean onRenderMessage(InMsg inMsg, MsgController msgController) {
      
        return true;
    }
}
```
**说明：**

* 1、在任意maven module下，编写任意名称的类，实现WechatAddon接口。JPress 会自动扫描到该类，并添加到 JPress 的管理体系里去。
* 2、复写方法`onMatchingMessage`和`onRenderMessage`。
    * onMatchingMessage ：用来匹配是否是本插件要处理的消息
    * onRenderMessage ：用来返回给微信客户端一个消息

* 3、添加 `@WechatAddonConfig` 注解的配置，用来给这个插件添加描述。

以下代码是完整的 hello world 例子，当用户在微信客客户端给公众号输入 `hello` 的时候，服务器给微信返回 `world` 字符串：

```java
@WechatAddonConfig(
        id = "ip.press.helloaddon", //这个插件的ID
        title = "Hello World",//这个插件的标题，用于在后台显示
        description = "这是一个 Hello World 微信插件，方便开发参考。用户输入 hello，返回 world", //这个插件的描述
        author = "海哥" //这个插件的作者
)
public class HelloWechatAddon implements WechatAddon {

    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        
        //当用户给公众号发送的不是文本消息的时候
        //返回 false 不由本插件处理
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        
        //当用户输入的内容不是 hello 的时候
        //返回false，不由本插件处理
        return content != null && content.equalsIgnoreCase("hello");
    }


    @Override
    public boolean onRenderMessage(InMsg inMsg, MsgController msgController) {
    
        //创建一个新的文本消息
        //通过 msgController 进行渲染返回给用户
        OutTextMsg outTextMsg = new OutTextMsg(inMsg);
        outTextMsg.setContent("world");
        msgController.render(outTextMsg);
        
        //返回 true，表示本插件已经成功处理该消息
        //若返回false，表示本插件处理消息失败，将会交给系统或者其他插件去处理
        return true;
    }
}
```
完整代码可以看这里：https://gitee.com/fuhai/jpress/blob/master/jpress-web/src/main/java/io/jpress/web/wechat/HelloWechatAddon.java


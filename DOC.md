# JPress文档



## 开始
演示站点：[http://www.yangfuhai.com](http://www.yangfuhai.com) （也是作者博客）

JPress官网：[http://jpress.io](http://jpress.io) 

### 简介
JPress，一个wordpress的java代替版本，使用JFinal开发。支持类似wordpress的几乎所有功能，比如：模板，插件等。同时在模板上，JPress提出了“模板即模型”的概念，方便模板制作人灵活制作业务模型，移除了widget等繁杂功能，同时在模板和插件制作上比wordpress更加灵活简洁。

但是，JPress又不是wordpress的java版本，它天生融合了微信公众平台，整合了国内众多云平台、短信发送、邮件发送平台，独创的“模板即模型”概念是wordpress所不具备的，只有资深的wordpress玩家才能体会里面的微妙关系。同时后续会添加微信文章同步，QQ公众平台，今日头条，一点资讯等新媒体的文章同步功能，更加国产和本地化。


### 下载
目前JPress托管在github和git.oschina.net上，网址分别是：

* [https://github.com/JpressProjects/jpress](https://github.com/JpressProjects/jpress)
* [http://git.oschina.net/fuhai/jpress](http://git.oschina.net/fuhai/jpress)

可以通过以上网址查看JPress的更新情况。

目前下载可以进入以上两个网站直接通过浏览器下载ZIP压缩包，也可以通过以下git命令下载到本地。

<center> `git clone https://github.com/JpressProjects/jpress.git` </center>
或
<center> `git clone https://git.oschina.net/fuhai/jpress.git` </center>


## 部署
### 环境配置

#### JAVA环境配置
目前假定您已经配置好了JAVA环境，若不会配置JAVA环境，请自行通过搜索引擎搜索相关知识。

#### Maven环境配置
暂略...

### JPress安装

#### tomcat安装
暂略...

#### JPress安装
如果您已经在您的服务器配置好了相关应用，比如tomcat，nginx等，就可以进行JPress安装了。

主要有一下三个步骤：

##### 第1步：生成war包
下载到本地后，进入jpress-web目录，执行`mvn package`命令，稍等片刻，命令执行完毕后会在jpress-web目录生成一个jpress-web-1.0.war的war包（可能今后jpress升级后，版本可能不是1.0了），拷贝war包放到tomcat的webapp目录下即可运行。

##### 第2步：拷贝war到tomcat并运行
拷贝第一个生成的war包到tomcat的webapp目录，启动tomcat。

##### 第3步：浏览器访问tomcat，进行配置
当jpress第一次运行的时候，jpress会去检测`class path`目录下是否有db.properties数据库配置文件，如果没有该文件，证明jpress是第一次运行。当浏览器访问jpress时，jpress会自动跳转到安装页面，让用户进行安装。


#### JPress安装注意事项
1. JPress要求数据库 ***必须*** 在5.5以上，建议最好是`5.6`或`5.6以上`。
2. JPress在安装的过程中，需要用用户自己创建好数据库，JPress在安装的过程中只做建表工作。


## 后台管理

### 进入后台
当JPress安装完毕后，访问 `http://yoursite.com/admin`（本地：`http://127.0.0.1/admin`），输入安装配置的账号密码，即可进入管理后台。

### 内容发布
### 网站配置
#### 常规
#### 评论
#### 通知
#### SEO
#### 水印
#### 连接形式

### 微信相关

#### 自动回复
#### 默认回复
#### 菜单设置
#### 微信配置

微信配置需要JPress端配置和微信公众号端两者同时配置。
##### JPress端配置
1. 进入微信公众号管理后台 `http://mp.weixin.qq.com`，依次进入`开发` > `公众号设置`，查看AppID(应用ID)和AppSecret(应用秘钥)。
3. 进入JPress后台，找到`微信配置`页面，填写正确的AppID和AppSecret，同时token随意填写，比如填写`jpress`。

##### 微信公众号后台配置
1. 进入微信公众号管理后台 `http://mp.weixin.qq.com`，在`公众号设置`里找到`服务器配置`。
2. 服务器配置内容如下：

> **URL(服务器地址)**：`http://www.你的域名.com/wechat`，比如我的博客网址是：`http://www.yangfuhai.com`，那么就填写 `http://www.yangfuhai.com/wechat`

> **Token(令牌)**：填写`jpress`，此处要保证和你JPress后台的token里填写的一模一样。

> **EncodingAESKey(消息加解密密钥)**：随机生成。比如：trJAaCyaexHuLB1FsQ0QKjVFI3zFtQNRiJ5qkp9Hx1z

>**消息加解密方式**：明文模式



## 模板开发

开发一个全新的模板，主要有以下几个步骤：


 1、 建立一个空的文件夹，用来存放模板文件，一般文件夹的名字用英文。
 
 2、 在这个文件建立一个`tpl_config.xml`文件，用来配置模板的信息和模型。
  `tpl_config.xml`的内容如下：
  
```
<?xml version="1.0" encoding="UTF-8"?>
<config>
	<infos>
		<id>模板ID</id>
		<title>模板名称</title>
		<description>模板描述</description>
	</infos>
	
	<module title="文章" name="article" list="所有文章" add="撰写文章" comment="评论">
		<taxonomy title="专题" name="category" />
	</module>
	
</config>
```
说明：
> 1. 通过infos类配置模板的信息，其中模板的id在整个jpress中必须唯一。
> 2. 通过module来定义此模板支持的内容模型，
> 3. 通过taxonomy定义了内容支持的分类类别。可能有些会支持比如：标签、分类、小组等多种分类。
  
 3、 在这个文件建立一个`tpl_screenshot.png`图片，用来在JPress后台显示模板截图。
 
 4、 在这个文件建立`index.html`用来显示网站首页。`index.html`内容如下：
 
 ```
<!DOCTYPE html>
<html>
<head>
  <title>${WEB_NAME!}</title>
</head>
<body>
<!-- 此处的 module="article" 中的 article 要和模型中定义的name一致，表示读取该模型的内容-->
<@jp.indexPage module="article"> 
	<#list page.getList() as content>
		<a href="${content.url}">${content.title!}</a>
	</#list>
</@jp.indexPage >
</body>
</html>
 ```
 说明：
> 1. ${WEB_NAME!} 用来读取后台设置的网站名称。
> 2. \<@jp.indexPage> </@jp.indexPage> 用来读取内容的列表。 
> 3. <#list> </#list> 用来循环显示其包含的内容。
 
 5、把这个文件夹压缩成为`.zip`的压缩包，进入JPress后台，通过模板管理的 安装 功能安装完毕该模板后，就可以使用该模板了。
 
 
 
 到此，一个完整的模板制作流程制作完毕。如果不太明白，也可以先看下视频教程 ： [http://www.yangfuhai.com/post/22.html](http://www.yangfuhai.com/post/22.html)
 




###模板结构
JPress的模板主要分为如下几类：
> * 首页模板  (index.html)
> * 分类页面模板 (taxonomy.html)
> * 详情页模板 (content.html)
> * 错误页模板 (404.html)
> * 用户中心模板 (user_*.html)
> * 搜索结果模板 (search.html)


JPress的模板结构大概如下：

```
index.html
taxonomy.html
content.html
404.html
user_center.html
user_detail.html
user_login.html
user_register.html
page_test.html
tpl_config.xml
tpl_screenshot.png
tpl_setting.html
```

* **index.html** 首页
* **404.html** 404页面，当JPress有404错误的时候会渲染此页面。
* **content.html** 详情页，文章、论坛、商城等详情内容会调用此页面进行渲染。同时，content.html 可以扩展为例如：`content_article.html`、`content_bbs.html`、`content_article_blog.html`、`content_article_music.html` 等。扩展的规则为：`content_module_style.html` 其中的module为模型的名称，style为样式。当模板有文件有多个样式的时候，在发布内容（比如：文章）的时候会有一个样式的选择。当用户发布了内容，选择的样式为blog，那么，JPress会自动去找`content_article_blog.html`的页面进行渲染，若当模板中由于某些原因（比如：更改了模板）导致`content_article_blog.html`不存在了，JPress会自动去找`content_article.html`渲染，若`content_article.html` 也不存在了，JPress会自动找 `content.html`渲染，若`content.html`也不存在了，JPress会报404错误。
* **taxonomy.html** 分类页，和content.html一样，也可以扩展作为`taxonomy_article.html` 、`taxonomy_bbs.html`、`taxonomy_article_news.html`、`taxonomy_article_xxx.html` 等。其中，taxonomy.html的扩展规则为：`taxonomy_module_slug.html`,其中module为模型，slug为后台填写。
* **user_center.html** 用户中心
* **user_detail.html** 用户详情
* **user_login.html** 用户注册
* **user_register.html** 用户登录
* **page_test.html** 当访问`http://www.xxx.com/test`的时候回调用这个文件渲染。

其中以tpl_开头的是模板的系统文件。

* **tpl_screenshot.png** 模板缩略图
* **tpl_setting.html** 模板设置文件，后台的模板设置功能将会调用这个页面进行渲染。
* **tpl_config.xml** 模板配置文件



其中最为重要的是 **tpl_config.xml** 文件，tpl_config.xml是模板的核心配置文件，大概内容如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<config>
	<infos>
		<title>JBlog</title>
		<id>JBlog</id>
		<description>JBlog是JPress内置的博客模板，后续会持续升级。</description>
		<author>jpress</author>
		<authorWebsite>http://www.jpress.io</authorWebsite>
		<version>v1.0</version>
		<versionCode>1</versionCode>
		<updateUrl>http://www.jpress.io</updateUrl>
	</infos>

	<module title="文章" name="article" list="所有文章" add="撰写文章" comment="评论">
		<taxonomy title="分类" name="category" formType="select" />
		<taxonomy title="专题" name="feature" formType="select" />
		<taxonomy title="标签" name="tag" formType="input" />
		
		<metadata dataType="input" name="_meta1" text="元数据1"  placeholder="元数据测试1"/>
		<metadata dataType="input" name="_meta2" text="元数据2"  placeholder="元数据测试2"/>
	</module>

	<module title="页面" name="page" list="所有页面" add="新建页面" comment="评论">
	</module>

	<thumbnail name="t1" size="780 x 240" />
	<thumbnail name="t2" size="240 x 140" />
	<thumbnail name="t3" size="600 x 300" />
	<thumbnail name="t4" size="300 x 300" />

</config>
```
* infos 标签是模板的基本信息，其中id是整个JPress系统中具有唯一性，如果系统已经有了该ID，则这个模板无法安装成功。
* module 是JPress的“模板即模型”设计的设计理念的基本体现，在模板中有多少个模型，后台的管理就有多个模型的菜单管理。同时，module中的name属性在同一个模板中是具有唯一性的。
* thumbnail 是缩略图，name是缩略图的名称，size是缩略图的大写。模板配置好缩略图后，当用户或管理员上传图片，图片会被剪切成模板定义的缩略图。

###模板标签

JPress的标签分为全局标签和普通标签。

JPress标签的规则如下：

1. 全局标签全部使用大写；
2. 普通标签的属性名都是驼峰命名法；



### 全局标签：
全局标签又分为 数据标签和函数标签；

* 数据标签代表某个数值；
* 函数标签代表某个功能；

目前JPress提供的全局数据标签有如下：

```
REQUEST;
CPATH;
TPATH;
CTPATH;
SPATH;
JPRESS_VERSION;
WEB_NAME;
WEB_TITLE;
WEB_SUBTITLE;
META_KEYWORDS;
META_DESCRIPTION;
```

目前JPress提供的全局函数标签有如下：

```
OPTION('key');
OPTION_CHECKED('key','value');
```



###全局标签的使用

#### REQUEST
http的request对象，可以通过request获取请求的相关数据。

使用代码：

```
${REQUEST!} 
```
或者

```
${REQUEST.requestURI!} 
```

#### CPATH
ContextPath的简写，当jpress放在子目录访问的时候，可以通过CPATH获取耳机目录的路径；

使用代码：

```
${CPATH!} 
```

#### TPATH
模板路径，当目录文件指定到某CSS/JS时，可以通过添加TPATH,正确指定到相应文件。

使用代码：

```
${TPATH!} 
```
例如，在目录文件里的代如下：

```
 <link rel="stylesheet" href="${CPATH}/assets/css/app.css"/>
```
可以指定到当前目录目录的`/assets/css/app.css`下。

#### CTPATH
CTPATH = CPATH + TPATH ；

使用代码：

```
${CTPATH!} 
```

#### SPATH
静态文件目录；

#### JPRESS_VERSION
JPress版本

#### WEB_NAME
网站名称

#### WEB_TITLE
网页标题

#### WEB_SUBTITLE
网页子标题

#### META_KEYWORDS
网页关键字

#### META_DESCRIPTION
网页描述

#### OPTION
通过这个函数标签，可以读取后台的所以配置信息；也就是可以读取option数据库表的值。

使用代码：

```
${OPTION('web_name')!} <!--读取key为web_name的option配置-->
```


#### OPTION_CHECKED
通过这个函数标签，可以读取后台的所以配置信息，判断后台的值是否等于输入的值。

* 如果等于前台输入的值，这个标签这输出 checked="checked"
* 如果不等于，则输出 空；

使用代码：

```
${OPTION_CHECKED('web_name','杨福海的博客')!} 
```
如果后台配置option的`web_name`为`杨福海的博客`,那么`${OPTION_CHECKED('web_name','杨福海的博客')!} `这个代码就输出 `checked="checked"` ,否则输出空内容。



### 普通标签：
目前JPress提供的普通标签有如下，今后会一直更新，所以标签有可能一直在增加或修改：

```
IndexPageTag
ContentPageTag
CommentPageTag
ContentsTag
TaxonomysTag
TagsTag
UsersTag
UserContentPageTag
ModulesTag
MenusTag
ImageTag
NextContentTag
PreviousContentTag
```
其中：

* **IndexPageTag** 用于在首页或单页面，即 index.html模板或page_*.html模板
* **ContentPageTag** 内容列表页，也就是分类页，即taxonomy.html 或 taxonomy_*.html
* **CommnetPageTag** 评论分页列表，只能用在内容详情页。即content.html 或 content_*.html
* **UserContentPageTag** 用户内容列表，只能用户用户中心，用来显示用户`发布的`、`收藏的`和`评论的`内容。
* **ContentsTag** 文本列表（没有分页），可以用于任何页面
* **UsersTag** 用户列表（没有分页），可以用于任何页面
* **MenuTag** 网站菜单，可以用于任何页面
* **TaxonomysTag** 分类列表，可以用于任何页面
* **TagsTag** 标签列表，可以用于任何页面
* **NextContentTag** 下一篇内容，只能用于内容详情页
* **PreviousContentTag** 上一篇内容，只能用于内容详情页


###普通标签的使用


####IndexPageTag

IndexPageTag标签 只能用在首页即index.html和单页即page_*.html。

代码如下：

```
<@jp.indexPage module="article"> 
	<#list page.getList() as content>
		<a href="${content.url}">${content.title}</a> <br />
	</#list>
	
	<@pagination>
		<#list pages as page>
			<a href="${page.url}">${page.text}</a>
		</#list>
	</@pagination>
</@jp.indexPage>
```


代码解释：
> * \<@jp.indexPage> </@jp.indexPage> 是 **IndexPageTag** 标签的开头和结尾。
> * <#list> </#list> 是循环标签，会循环输出 **指定列表** 的所有内容，此标签属于freemarker模板引擎的自带标签。
> * \<@pagination> </@pagination> 是 **IndexPageTag** 标签的 **子标签**，用于显示 **页码** 列表。

indexPage标签属性：
> * module ： 指定读取模型的内容。在如上代码中`module="article"`表示读取article模型的内容。
> * pagesize ： 指定每页显示的条数，默认为10。
> * orderby ： 指定当前页面数据的排序方式，根据什么来排序。

备注：
> JPress的标签体系基于freemarker模板引擎的标签，


#### ContentPageTag 

ContentPageTag标签 和 IndexPageTag标签 的用法完全一样，唯一的区别是：
ContentPageTag标签 只能在分类页使用，即只能在taxonomy.html 或 taxonomy_*.html上使用。

代码如下：

```
<@jp.contentPage > 
	<#list page.getList() as content>
		<a href="${content.url}">${content.title}</a> <br />
	</#list>
	
	<@pagination>
		<#list pages as page>
			<a href="${page.url}">${page.text}</a>
		</#list>
	</@pagination>
</@jp.contentPage>
```

ContentPageTag标签支持的属性如下：
> * pagesize ： 每页显示的条数
> * orderby ：排序的字段或方法

#### UserContentPageTag 
UserContentPageTag标签 和 IndexPageTag标签 的用法完全一样，唯一的区别是：
UserContentPageTag标签 只能在用户中心使用，用于显示**登陆用户**的文章。

代码如下：

```
<@jp.userContentPage > 
	<#list page.getList() as content>
		<a href="${content.url}">${content.title}</a> <br />
	</#list>
	
	<@pagination>
		<#list pages as page>
			<a href="${page.url}">${page.text}</a>
		</#list>
	</@pagination>
</@jp.userContentPage>
```

UserContentPageTag标签支持的属性如下：
> * pagesize ： 每页显示的条数
> * orderby ：内容列表排序的字段或方法
> * module ：指定 **登陆用户** 发布的内容列表模型
> * taxonomyid ：指定 **登陆用户** 发布的内容列表的类型ID 或 tagId
> * status ： 指定 **登陆用户** 发布的内容列表的状态

#### CommnetPageTag
CommnetPageTag标签用于显示内容的回复列表（或叫评论列表），只能在内容详情页面使用，即只能在content.html 或 content_*.html 上使用。

代码如下：

```
<@jp.commentPage > 
	<#list page.getList() as content>
		<a href="${content.url}">${content.title}</a> <br />
	</#list>
	
	<@pagination>
		<#list pages as page>
			<a href="${page.url}">${page.text}</a>
		</#list>
	</@pagination>
</@jp.commentPage>
```

CommnetPageTag标签支持的属性如下：
> * pagesize ： 每页显示的条数。

#### ContentTag
在任意地方显示谋篇文章内容。

代码如下：

```
<@jp.content id="123"> 
	下一篇：<a href="${content.url}">${content.title}</a>
</@jp.content>
```

#### ContentsTag
文章列表标签，可以在任意页面使用此标签。此标签不带分页功能。

代码如下：

```
<@jp.contents module="article" count="3" orderBy="comment_count" hasThumbnail="true"> 
	<#list contents as content>
		<a href="${content.url}">${content.title}</a> <br />
	</#list> 
</@jp.contents>
```

代码解释：
> * `module="article"` 表示内容列表为`article`模型的内容。
> * `count="3"` 表示内容列表的数量为3条内容。
> * `orderby="comment_count"` 表示内容列表根据`comment_count`字段排序。
> * `hasThumbnail="true"`表示内容列表必须有缩略图。

ContentsTag标签支持的属性如下：
> * module ：指定内容列表的模型
> * style ： 指定内容列表的样式。
> * flag ： 指定内容列表的flag标示。
> * userId ： 指定内容列表的用户，即哪个用户发布的内容。
> * parentId ： 
> * hasThumbnail ：指定内容列表是否必须包含或不包含缩略图。
> * tag ： 指定哪个tag的内容列表。
> * typeSlug ：指定哪个分类下的内容列表。通过分类的slug来指定。
> * typeId ： 指定哪个分类下的内容列表。通过分类的id来指定。
> * keyword ： 指定哪个关键字的内容列表。
> * orderby ： 指定内容列表的排序方式。
> * count ： 指定内容列表的总数量。

#### TaxonomyTag
用于读取某个分类信息。

代码如下：

```
<@jp.taxonomy id="1" > 
	<a href="${taxonomy.url}">${taxonomy.title}</a>
</@jp.taxonomy>
```

#### TaxonomysTag
用于显示分类内容。

代码如下：

```
<@jp.taxonomys>
	<#list taxonomys as taxonomy>
		<a href="${taxonomy.url!}">${taxonomy.title!}</a>
	</#list>
</@jp.taxonomys>
```
#### TagsTag
用于显示TAG内容。

代码如下：

```
<@jp.tags>
	<#list tags as tag>
		<a href="${tag.url!}">${tag.title!}</a>
	</#list>
</@jp.tags>
```
#### UsersTag

#### MenusTag
网站菜单的标签。可以在任意页面使用，用于显示网站菜单导航。

代码如下：

```
<@jp.menus>
	<#list menus as menu>
		<li >
	        <a  href="${menu.url!}">
	        	${menu.title!}
        		<#if menu.isActive() >
        			<span class="x-a-border"></span>
        		</#if>
        	</a>
       </li>
	</#list>
</@jp.menus>
```
代码解释：

```
<#if menu.isActive() >
	<span class="x-a-border"></span>
</#if>
```
这段代码表示当前页面是否属于该菜单下的内容，如果属于该菜单，则输出`<span class="x-a-border"></span>`，不属于则不输出。常用来显示导航高亮。


#### NextContentTag
该标签只能在内容详情页面使用，用来显示下一篇内容。

代码如下：

```
<@jp.next> 
	下一篇：<a href="${content.url}">${content.title}</a>
</@jp.next>
```

#### PreviousContentTag
该标签只能在内容详情页面使用，用来显示上一篇内容。

代码如下：

```
<@jp.previous> 
	上一篇：<a href="${content.url}">${content.title}</a>
</@jp.previous>
```

###模板设置

## 插件开发
### helloworld


```
public class HelloAddon extends Addon {

	/**
	 * AddonController 请求的钩子
	 * @param controller
	 */
	@Hook(Hooks.PROCESS_CONTROLLER)
	public Render hello(Controller controller) {
		// 访问 http://127.0.0.1:8080/addon 看到效果
		return new TextRender("hello addon");
	}

	@Override
	public boolean onStart() {
		MessageKit.register(HelloMessage.class);
		return true;
	}

	@Override
	public boolean onStop() {
		MessageKit.unRegister(HelloMessage.class);
		return true;
	}

}
```
以上是一个插件最简单的例子。
当该插件被安装，并启动的时候，该插件的`onStart()`方法会被触发，当该插件被停止的时候，`onStop()`会被触发，我们可以在`onStart()`做插件的一些初始化操作，在`onStop()`做些资源释放的操作。

可以通过@Hook(Hooks.PROCESS_CONTROLLER)注解，让我们的方法去注册到某个`钩子`,当某逻辑执行的时候，会自动执行到该方法。

### 规范
### 钩子

## 高级
### 二次开发
##### 规范
在二次开发中，不要修改JPress的任何代码，而是建立自己的一个maven module模块，通过pom依赖的方式把jpress相关代码导入到自己的module中。

maven模块的pom文件如下：

```
<?xml version="1.0"?>
<project
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
	xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<modelVersion>4.0.0</modelVersion>

	<parent>
		<groupId>io.jpress</groupId>
		<artifactId>jpress</artifactId>
		<version>1.0</version>
	</parent>

	<packaging>war</packaging>
	<artifactId>jpress-your-modulen-ame</artifactId>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

	<dependencies>
		<dependency>
			<groupId>com.jfinal</groupId>
			<artifactId>jfinal</artifactId>
		</dependency>
		
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<scope>provided</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-utils</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-oauth2</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-message</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-search-api</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-search-dbsimple</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-consts</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-model</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-core</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<classifier>classes</classifier>
			<scope>provided</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-core</artifactId>
			<version>1.0</version>
			<type>war</type>
			<scope>compile</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-front</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<classifier>classes</classifier>
			<scope>provided</scope>
		</dependency>
		
		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-front</artifactId>
			<version>1.0</version>
			<type>war</type>
			<scope>compile</scope>
		</dependency>

		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-admin</artifactId>
			<version>1.0</version>
			<type>jar</type>
			<classifier>classes</classifier>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>io.jpress</groupId>
			<artifactId>jpress-web-admin</artifactId>
			<version>1.0</version>
			<type>war</type>
			<scope>compile</scope>
		</dependency>

	</dependencies>


	<build>
		<finalName>${project.artifactId}-${project.version}</finalName>
		<resources>
			<resource>
				<directory>src/main/config</directory>
				<includes>
					<include>**/*.*</include>
				</includes>
				<filtering>false</filtering>
			</resource>
			<resource>
				<directory>src/main/language</directory>
				<includes>
					<include>**/*.*</include>
				</includes>
				<filtering>false</filtering>
			</resource>
		</resources>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<encoding>UTF-8</encoding>
				</configuration>
			</plugin>


			<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
				<artifactId>tomcat6-maven-plugin</artifactId>
				<version>2.2</version>
				<configuration>
					<port>8080</port>
					<path>/jpress</path>
					<uriEncoding>UTF-8</uriEncoding>
					<server>tomcat7</server>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
				<artifactId>tomcat7-maven-plugin</artifactId>
				<version>2.2</version>
				<configuration>
					<port>8080</port>
					<path>/jpress</path>
					<uriEncoding>UTF-8</uriEncoding>
					<server>tomcat7</server>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.mortbay.jetty</groupId>
				<artifactId>maven-jetty-plugin</artifactId>
				<version>6.1.10</version>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<configuration>
					<encoding>utf-8</encoding>
					<packagingExcludes>WEB-INF/web.xml</packagingExcludes>
					<overlays>
						<overlay>
							<groupId>io.jpress</groupId>
							<artifactId>jpress-web-core</artifactId>
						</overlay>
						<overlay>
							<groupId>io.jpress</groupId>
							<artifactId>jpress-web-front</artifactId>
						</overlay>
						<overlay>
							<groupId>io.jpress</groupId>
							<artifactId>jpress-web-admin</artifactId>
						</overlay>
					</overlays>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>

```
这样，你的module就有了一个具体的web功能，你可以在里面添加自己的controller，model以及其他任何代码。此时，我们在运行我们的程序的时候，是运行我们自己新建的这个module，不再是jpress-web了，放到服务器运行的war包也是我们这个module，不再是jpress-web。

#### JPress的消息机制
#####后台菜单的定义
JPress在初始化后台菜单的时候，会发送一个初始化菜单的消息，所以我们在操作（添加、修改、删除）后台菜单的时候，只需要编写一个监听器，监听后台菜单的初始化，并对其操作即可。

如下代码：

```
@Listener(action = MenuManager.ACTION_INIT_MENU, async = false, weight = Listener.DEFAULT_WEIGHT + 1)
public class MenuInitListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		MenuManager manager = message.getData();

		manager.removeMenuGroupById("wechat"); //移除后台微信菜单
		manager.removeMenuGroupById("tools"); //移除后台的工具菜单
		manager.removeMenuGroupById("addon"); //移除后台的插件菜单

		MenuGroup templateMenu = manager.getMenuGroupById("template"); //获得后台的模板菜单
		templateMenu.removeMenuItemById("list"); //移除后台的 模板列表菜单
		templateMenu.removeMenuItemById("install"); //移除后台的 安装模板菜单
		
		//添加模板的首页设置菜单
		templateMenu.addMenuItem(0,new MenuItem("index", "/admin/index/setting", "首页设置")); 

	}

}
```

#####系统启动初始化
通过消息机制，JPress在启动的时候，会发送启动的消息，若在二次开发的过程中，需要在系统启动的时候做些自己业务逻辑相关的初始化工作，只需要如下代码即可：

```
@Listener(action = Actions.JPRESS_STARTED)
public class StartedListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		System.out.println(">>>>>>>>>>>>系统启动了");
		
		//添加自己的自定义标签
		Jpress.addTag("slider", new SliderTag());
		
	}
}

```

### 数据库操作
关于jpress原始的数据库，jpress已经提供了 xxxQuery的类来操作了，如果不能满足你的需求，给我提交反馈即可。
若在jpress二次开发中新增了自己的表，可以通过jpress-model里的`io.jpress.code.generator`来生成自己的代码。

## 关于
### 开发者
### JPress
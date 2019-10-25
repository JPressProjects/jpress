# 模板开发

## 目录

- 模板开发准备
- 模板的组成
- 模板的目录结构
- 模板的多样式
- 模板的手机版样式
- 模板引擎指令
- 模板标签
- 模板的安装和卸载

## 模板开发准备

**1、安装Docker**

JPress 模板开发准备，主要是准备 docker 环境，Docker 的安装可以参考一下链接：

* [Mac](https://docs.docker.com/docker-for-mac/install)
* [Windows](https://docs.docker.com/docker-for-windows/install)
* [Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu)
* [Debian](https://docs.docker.com/install/linux/docker-ce/debian)
* [CentOS](https://docs.docker.com/install/linux/docker-ce/centos)
* [Fedora](https://docs.docker.com/install/linux/docker-ce/fedora)
* [其他 Linux 发行版](https://docs.docker.com/install/linux/docker-ce/binaries)

Docker 的安装过程略，一般情况下都是安装比较顺利的，若安装处错自行搜索引擎查询下。

**2、通过Docker运行JPress**

Docker 安装完毕后，下载文件 https://gitee.com/fuhai/jpress/raw/v2.0/docker-compose-for-dev.yml 到本地。（打开此链接，然后右键另存为... 或者按快捷键 `ctrl+s` 保存到本地，mac系统的快捷键是`command+s`）

下载完毕后，重命名刚刚下载文件 `docker-compose-for-dev.yml` 为 `docker-compose.yml` ，并把 `docker-compose.yml` 放入提前建好的文件夹中。

然后通过 shell （ window系统是cmd ）进入该文件夹，执行如下命令：

`docker-compose up -d`

**Shell小知识**
>如何用命令进入某个文件夹？
>答：进入文件夹的命令是： `cd 文件夹的路径`


稍等片刻，命令执行完毕后，就可以通过 `http://127.0.0.1:8080` 进行访问 JPress了，此时浏览器会出现 JPress 安装向导，一路 `下一步` 就可以。

同时，在 `docker-compose.yml` 所在的同级目录下会出现一个 `templates` 等目录，我们开发自己模板的时候，需要把自己的模板放到`templates` 目录，然后开始开发。

**Docker小知识**
>1、通过 Docker Compose 命令启动 JPress 后如何停止？
>答：通过shell进入 docker-compose.yml 所在的目录，然后执行：`docker-compose down` 命令
>
>2、如何查看 JPress 运行的日志信息？
>答：先通过 `docker ps` 命令查看 JPress 的 container id，然后执行命令：`docker logs -f 查看到的container-id`



## 模板的组成

一个完整的模板，文件内容如下：

```
mytemplate
├── article.html
├── artlist.html
├── css
│   └── xxx.css
├── img
│   ├── xxx.png
│   ├── xxx.jpg
├── index.html
├── page.html
├── screenshot.png
├── setting.html
└── template.properties
```

其中 `index.html` 、 `screenshot.png` 、`template.properties` 是模板的必须文件，一个模板最少由着三个文件组成。

- index.html : 网站首页的模板
- screenshot.png : 后台的模板缩略图
- template.properties 模板的配置信息

>当我们开始开发一个新的模板的时候，可以先用这个三个文件，然后通过 JPress 后台进行安装，看一下效果。

模板的配置文件 `template.properties` 内容如下
  
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

* id ：模板ID，全网唯一，建议用 `域名+名称` 的命名方式
* title ：模板名称
* description ：模板简介
* anthor ：模板作者
* authorWebsite ：模板作者的官网
* version ：版本（不添加默认为1.0.0）
* versionCode ：版本号（只能是数字，不填写默认为1）
* screenshot ：此模板的缩略图图片（不填写默认为：screenshot.png）


## 模板的目录结构

- **index.html** ：首页模板
- **error.html** ：错误页面模板当系统发生错误的时候，会自动调用此页面进行渲染，也可以扩展为 error_404.html，当发生404错误的时候优先使用此文件，同理可以扩展 error_500.html ，当系统发生500错误的时候调用此文件渲染。 
- **setting.html** ：后台的模板设置页面，当次html不存在的时候，用户进入后台的模板设置，会显示此模板不支持设置功能 
- **screenshot.png** ：模板缩略图，用于在后台的模板列表里显示的图片 
- **template.properties** ：模板信息描述文件 
- **page.html** ：页面模块的模板，page.html 可以扩展为 page_aaa.html 、page_bbbb.html ，当模板扩展出 page_xxx.html 的时候，用户在后台发布页面内容的时候，就可以选择使用哪个模板样式进行渲染。例如： page_xxx.html 其中 `xxx` 为样式的名称。
- **article.html** ：文章详情模板， 和page模块一样，article.html 可以扩展出 article_styel1.html、article_style2.html，这样，用户在后台发布文章的时候，可以选择文章样式。（备注：用户中心投稿不能选择样式）  
- **artlist.html** ：文章列表模板， 和page、article一样，可以通过样式 
- **artsearch.htmll** ：文章搜索结果页，用于显示文章的搜索结果。 
- **user_login.html** ：用户登录页面，JPress已经内置了登录页面，但是，当模板下有 user_login.html 的时候，就会自动使用模板下的这个页面来渲染 
- **user_register.html** ：用注册页面，用法通同 user_login.html 
- **user_detail.html** ：用户详情页，一般用于显示某个用户的用户信息，用在 http://127.0.0.1/user/用户id 这个页面渲染。 



**备注：**
>所有的模板文件都可以扩展出专门用于渲染手机浏览器的模板。
>例如：首页的渲染模板是 `index.html` ，如果当前目录下有 `index_h5.html`，那么，当用户通过手机访问网站的时候，JPress 会自动使用 `index_h5.html` 去渲染。 page 和 article、artlist 同理。






## 模板引擎指令

有了以上这些目录结构，我们实际上不用编写任何的标签，就可以制作模板了，只是做出来的模板是静态模板，不能读取后台数据。

要读取后台的数据，需要用到模板标签。只有通过在静态的html上，添加 JPress 标签，才能可以读取后台数据。

在学习 JPress 模板标签之前，我们需要了解模板引擎的几个基本用法。

1、输出功能：

```
#(content ??)
```
2、循环功能：

```
#for(item : items)  
    //...do something
#end
```
3、判断功能：

```
#if(...) 
    //...do something
#elseif(...)  
    //...do something
#else   
    //...do something
#end
```

更多的功能和文档，请参考 JPress 模板引擎文档：https://www.jfinal.com/doc/6-4


## 模板标签

JPress的模板标签，分为以下三种：

- 1、全局对象
- 2、数据指令


#### 全局对象

| 名称 | 数据类型 | 标签描述 |  
| --- | --- | --- | 
| #(WEB_NAME ??) | 字符串 |  网站名称 |  
| #(WEB_TITLE ??) |  字符串 | 网站标题 |  
| #(WEB_SUBTITLE ??) |  字符串 | 网站副标题 | 
| #(WEB_DOMAIN ??) |  字符串 | 网站域名 | 
| #(WEB_COPYRIGHT ??) |  字符串 | 网站版权信息 | 
| #(SEO_TITLE ??) |  字符串 | 网站SEO标题 | 
| #(SEO_KEYWORDS ??) |  字符串 | 网站SEO关键字 | 
| #(SEO_DESCRIPTION ??) |  字符串 | 网站SEO描述 | 
| MENUS  | 数据列表( list ) | 菜单数据 | 
| USER  | 对象( object ) | 已经登录的用户对象 | 
| CSRF_TOKEN  | 字符串 | 当进行数据操作的时候必须要传入这个参数 | 

标签描述，标签建议用 `#( 名称 ??)` 的方式来读取数据，而不是用 `#(名称)` 两个问号（??）的意思是如果 后台填写的名称为空格，那么就用 两个问号（??）之后的内容来显示。

例如： 
`#(WEB_NAME ??)` 表示优先使用 WEB_NAME 来显示，但是当 WEB_NAME 为空的时候，显示空数据（因为两个问好（??）之后的内容为空）。

`#(WEB_NAME ?? WEB_TITLE)` 表示优先使用 WEB_NAME 来显示，但是当 WEB_NAME 为空的时候，用 WEB_TITLE（网站标题） 来显示。

`#(SEO_TITLE ?? WEB_TITLE +'-'+ WEB_SUBTITLE)` 表示优先使用 SEO_TITLE（SEO标题） 来显示，但是当 SEO_TITLE 为空的时候，用 `WEB_TITLE - WEB_SUBTITLE` 来显示。

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

```html
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

对于以上的菜单显示，还有一个问题就是如何显示二级菜单呢？

代码如下：

```html
#for(menu : MENUS)
    <li> <a href="#(menu.url ??)">#(menu.text ??)</a> </li>
    #if(menu.hasChild())
        <div class="二级菜单的class">
        #for(childMenu : menu.getChilds())
            <li> <a href="#(menu.url ??)">#(menu.text ??)</a> </li>
        #end
    </div>
    #end
#end
```

以上代码显示了所有菜单的的二级菜单，但是，有些时候我们想在网站的某些位置，显示 **当前菜单** 下的子菜单，如何做呢？


代码如下：

```html
#for(me: MENUS)
    #if(me.isActive && me.hasChild())
        <h3 class="menut-title">#(me.text ??)</h3>
        <ul class="inner-menut">
            #for(m : me.getChilds())
                <li class="#(m.isActive ? 'active' : '')">
                    <a href="#(CPATH)#(m.url ??)">
                    #(m.text ??)
                    </a>
                </li>
            #end
        </ul>
    #end
#end
```

在很多模板中，往往需要判断当前用户是否已经登录，用来对模板进行特定的展示。

代码如下：

```
#if(USER)
    #(USER.nickname ??) 欢迎回来，头像：#(USER.avatar ??)
 #else
    请登录
 #end
```
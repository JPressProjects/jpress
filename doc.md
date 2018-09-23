### JPress 文档


#### JPress模板制作教程

##### 描述
  
  JPress模板是由一些列 html、css、js 和 JPress 标签组成的文件目录。
  
  JPress 目录结构如下：
  
  * index.html 首页模板
  * page.html 页面默认模板
  * article.html 文章详情的默认模板
  * articlecategory.html 文章类别的默认模板
  * setting.html 模板设置页面
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
 JPress是基于Jfinal和Jboot进行开发的，使用的模板引擎是Jfinal Enjoy，Jfinal Enjoy 模板引擎也是世界上最好的模板引擎，
 拥有极好的性能和开发体验，在学习JPress模板制作之前，很有必要学习一下Jfinal Enjoy 模板引擎。
 
 1. 菜单标签：#menu()
 
 
    使用代码如下：
    
```html
#menus("main")
    #for(menu : menus)
        <li class="nav-item">
            <a href="#(menu.url ??)">#(menu.text ??)</a>
        </li>
    #end
#end
```

 2. 文章分类标签：#articlePage()
  
  使用代码如下：
  
  ```html
#articlePage()

    #for(article : articlePage.list)
        <a href="#articleUrl(article)">#(article.title ??)</a>
    #end
    
    #articlePaginate()
            #for(page : pages)
                <a href="#(page.url ??)">#(page.text ??)</a>
            #end
     #end
     
#end
```
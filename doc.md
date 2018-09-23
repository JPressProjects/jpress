### JPress 文档


#### JPress模板制作教程

##### 描述
  
  JPress模板是由一些列 html、css、js 和 JPress 标签组成的文件目录。
  
  JPress 目录结构如下：
  
  * index.html 首页模板
  * page.html 页面默认模板
  * article.html 文章详情的默认模板
  * articlecategory_html 文章类别的默认模板
  
  * setting.html 模板设置页面
  * template.properties 模板信息配置
  * screenshot.png 缩略图
  
  其中，template.properties 文件配置如下
  
 ```java
 #模板ID，全网唯一，建议用域名+名称的命名方式
id=cn.jeanstudio.bluelight
#模板名称
title=BlueLight
#模板描述
description=BlueLight 是设JeanStudio工作室为JPress设计的官网模板
#模板作者
anthor= jeanStudio
#模板作者官网
authorWebsite= http://www.jeanstudio.cn
#模板版本
version=1.0
#模板版本号
versionCode=1
#模板升级的url地址
updateUrl=
#缩略图图片名称
screenshot=screenshot.png
```

##### 模板标签
 JPress是基于Jfinal和Jboot进行开发的，使用的模板引擎是Jfinal Enjoy，Jfinal Enjoy 模板引擎也是世界上最好的模板引擎，
 拥有极好的性能和开发体验，在学习JPress模板制作之前，很有必要学习一下Jfinal Enjoy 模板引擎。
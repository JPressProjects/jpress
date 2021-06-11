# JPress 的全局变量

此章节需要 JPress 模板语法基础知识的支持，在阅读这篇文档之前，请务必先了解下 [《模板语法》](./grammar.md)。

| 名称 | 数据类型 | 标签描述 |  
| --- | --- | --- | 
| WEB_NAME  | 字符串 |  网站名称 |  
| WEB_TITLE  |  字符串 | 网站标题 |  
| WEB_SUBTITLE  |  字符串 | 网站副标题 | 
| WEB_DOMAIN  |  字符串 | 网站域名 | 
| WEB_COPYRIGHT  |  字符串 | 网站版权信息 | 
| WEB_IPC_NO  |  字符串 | 网站备案号 | 
| SEO_TITLE  |  字符串 | 网站SEO标题 | 
| SEO_KEYWORDS  |  字符串 | 网站SEO关键字 | 
| SEO_DESCRIPTION  |  字符串 | 网站SEO描述 | 
| MENUS  | 数据列表( list ) | 菜单数据 | 
| USER  | 对象( object ) | 已经登录的用户对象 | 
| CSRF_TOKEN  | 字符串 | 当进行数据操作的时候必须要传入这个参数 | 
| C  | Controller 对象 | 可以通过 Controller 去读取 attribute、request、session、<br /> parameter 等信息，备注：只能用于读取，不能用于设置。 | 
| VERSION  | 字符串 | JPress 当前的版本号。 | 



## 输出网站标题、SEO关键字和SEO描述

```
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

## 输出网站菜单

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



## 判断当前用户是否登录

```
#if(USER)
    #(USER.nickname ??) 欢迎回来，头像：#(USER.avatar ??)
 #else
    请登录
 #end
```

## 退出登录

```
<a href="#(CPATH)/user/logout?csrf_token=#(CSRF_TOKEN)"> 退出登录 </a>
```







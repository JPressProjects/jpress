
## JPress 官网

http://www.jpress.io

## JPress 文档 

[点击这里](./doc/readme.md)

## JPress School 

https://mp.weixin.qq.com/s/GbNv0xaK6jruWqTDJ_Ospw

    
## 新版本相关视频教程

* **1. JPress下载、编译、运行** 

https://pan.baidu.com/s/1Pe0KcYcQGalxPnlUNw9rmg

## JPress 微信小程序SDK

网址：https://gitee.com/fuhai/jpress-miniprogram-sdk


## 常见问题

**问题1： 初始化数据表为没有任何数据 ？**

答：JPress 在初次正常运行的时候，会引导用户通过web页面去初始化基本数据，包括网站信息和管理员账号等。

**问题2：为什么发布文章是乱码？**

答：创建数据库的时候，注意编码要选择 `utf8mb4`，如果还出现乱码，请添加下jdbc链接的编码配置。例如：

```
jboot.datasource.type=mysql
jboot.datasource.url=jdbc:mysql://127.0.0.1:3306/jpress-master?useUnicode=true&characterEncoding=UTF-8
jboot.datasource.user=root
jboot.datasource.password=
```

**问题3：eclipse 或者 idea 调试的时候，为什么后台无法接受参数**

答：请先查看下JFinal文档 http://www.jfinal.com/doc/3-3 ，确认下自己的开发工具是否配置正确。

**问题4：为什么无法对文章进行评论**

答：JPress的文章评论功能是关闭的，请先在后台 `文章 -> 设置` 开启评论功能。


**更多问题，请关注 JPress 公众号 ：**

![](./doc/images/jpress_qrcode.jpg)


**JPress后台截图**

![](./doc/images/screenshot/001.png)

![](./doc/images/screenshot/002.png)

![](./doc/images/screenshot/003.png)

![](./doc/images/screenshot/004.png)

![](./doc/images/screenshot/005.png)

![](./doc/images/screenshot/006.png)

![](./doc/images/screenshot/007.png)

![](./doc/images/screenshot/008.png)

![](./doc/images/screenshot/009.png)

![](./doc/images/screenshot/010.png)

![](./doc/images/screenshot/011.png)

![](./doc/images/screenshot/012.png)

![](./doc/images/screenshot/013.png)

**用户中心的相关截图**

![](./doc/images/screenshot/014.png)

![](./doc/images/screenshot/015.png)

![](./doc/images/screenshot/016.png)

![](./doc/images/screenshot/017.png)




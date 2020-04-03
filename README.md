![](./doc/images/screenshot.png)


<h1 align="center"><a href="http://www.jpress.io" target="_blank"> JPress </a></h1>

<p align="center">
一个使用 Java 开发的类似 WordPress 的产品，并在此基础上增加了电商的功能。
</p>


<p align="center">
<img alt="release" src="https://img.shields.io/github/release/JpressProjects/jpress.svg?style=flat-square"/>
<img alt="release" src="https://img.shields.io/github/release-date/JpressProjects/jpress.svg?style=flat-square"/>
<img alt="commit" src="https://img.shields.io/github/last-commit/JpressProjects/jpress.svg?style=flat-square"/>
<img alt="issues" src="https://img.shields.io/github/issues-closed/JpressProjects/jpress.svg?style=flat-square"/>
<img alt="license" src="https://img.shields.io/github/license/JpressProjects/jpress.svg?style=flat-square"/>
</p>

## 功能

#### 内容相关
- 文章管理
- 文章分类
- 文章标签
- 文章搜索（支持 sql like、Lucene、es、OpenSearch）
- 用户投稿
- 页面管理
- 评论管理
- 附件管理


#### 电商相关
- 产品管理
- 产品分类
- 产品标签
- 产品搜索（支持 sql like、Lucene、es、OpenSearch）
- 产品分销
- 会员管理
- 订单管理
- 分销管理
- 提现管理
- 优惠券管理
- 支付配置
- 物流配置


#### 用户相关
- 用户管理
- 会员管理
- 权限管理
- 订单管理
- 用户标签
- 短信群发
- 邮件群发


#### 系统相关
- 模板管理
- 插件管理
- 微信管理
- 系统管理


## 特点

#### 模板

- 模板在线安装、卸载
- 模板在线启用、切换
- 在线编辑及实时生效
- 完善的模板开发文档
- 极致的模板开发体验


#### 插件

- 插件在线安装、卸载
- 插件在线启用、停止
- 插件在线更新
- 支持在插件里添加新的 Controller
- 支持在插件里添加新的 Handler
- 支持在插件里添加新的 Interceptor
- 支持在插件里添加新的 Html、Css 和 Js
- 支持在插件里创建新的数据库表以及对应的 Model
- 支持在插件里链接不同的数据库
- 支持通过插件动态扩展后台菜单和用户中心菜单
- 插件扩展的菜单支持用户权限设置的管理
- 插件被停止：该插件的所有Controller、Handler、Intercepter 自动被移除
- 插件被卸载：该插件的所有资源全部被删除


#### 用户

- 独立登录、注册入口
- 手机短信、邮箱激活功能
- 用户中心（投稿、文章管理、评论管理、个人资料管理等）
- 第三方登录：微信、QQ、钉钉、oschina、GitHub等
- 微信浏览时，通过微信授权自动获取用户信息


#### 角色和权限

- 角色管理
- 全自动、免维护的权限字典（自动发现后台路由、插件安装卸载自动分配对应）
- 角色和权限的分配
- 用户多角色功能
- 超级管理员


#### 微信

- 微信公众号对接
- 微信公众号关键字自动回复
- 微信公众号菜单设置
- 微信公众号运营插件
- 通过运用插件灵活扩展各种微信营销功能
- 微信小程序对接、和配置


#### SEO

- 每篇文章和页面独立的SEO设置
- Baidu API 的实时推送
- Baidu 和 Google 的自动 Ping 提交
- Sitemap 自动生成、后台支持自定义的开启和关闭
- robots.txt 爬虫蜘蛛的支持
- 整站伪静态支持，支持自定义开后缀


#### 其他

- WordPress、Hexo、Jekyll、微信公众号等文章一键导入
- 编写文章随意切换 CKEditor 和 Markdown 编辑
- 最大化、沉侵式的文章编写体验
- Docker 一键部署
- 阿里云、腾讯云 CDN 在线配置
- 阿里云、腾讯云短信验证（用户注册手机验证）
- 附件自动可配置自动同步阿里云 OSS
- 完善的API接口配置管理
- ... （更多等你发现）


## 交流

- 官网：[http://www.jpress.io](http://www.jpress.io)
- 论坛社区：[点击这里](http://www.jpress.io/club)
- 插件列表：[点击这里](http://www.jpress.io/article/category/plugin)
- 模板列表：[点击这里](http://www.jpress.io/article/category/template)
- QQ群：591396171 ，288397536


## 帮助文档

- [了解JPress](http://www.jpress.io)
- [快速开始](http://www.jpress.io/article/34)
- [安装](http://www.jpress.io/article/34)
- [升级](./doc/upgrade.md)
- [使用](./doc/manual.md)
- [模板开发](http://www.jpress.io/article/39)
- [二次开发](http://www.jpress.io/article/68)
- [插件开发](http://www.jpress.io/article/54)
- [微信运营插件开发](http://www.jpress.io/article/65)
- [微信小程序开发](http://www.jpress.io/article/67)
- [视频教程](http://www.jpress.io/article/category/course)
- [常见问题](./doc/faq.md)
- [JPress-VIP 会员](./doc/vip.md)

## 运行JPress


**在 Docker 上运行**

```
curl -O https://gitee.com/fuhai/jpress/raw/master/docker-compose.yml && docker-compose up -d
```

**在 Linux 上一键安装**

```
wget https://gitee.com/fuhai/jpress/raw/master/install.sh && bash install.sh
```

> 视频教程链接: https://pan.baidu.com/s/1ciA2DglE-JV-YiU3ojtmew 提取码: 37g5


**通过 Eclipse 或者 Idea 等开发工具运行**

- 1、在电脑安装好 Java、Maven 等开发环境
- 2、将源码下载、并导入 eclipse 或者 idea 
- 3、在项目的**根目录**，执行 `mvn clean install` 命令进行编译
- 4、在开发工具，右键运行 `starter/src/main/java/io.jpress.Starter` 下的 `main()` 方法
- 5、通过浏览器访问 `http://127.0.0.1:8080`，进行自动安装


> 可能遇到的问题： 
> 
> 1、执行 `mvn clean` 后，再次运行 JPress，JPress 会重新走安装流程。
>
> 解决方案： jpress 在安装过程中，会在 `starter/target/classes` 目录下生成的 `jboot.properties` 和 `install.lock` 文件，我们需要把这两个文件复制到 `starter/src/main/resource` 目录下。 因为，jpress 是否安装决定在这两个文件，当我们执行  `mvn clean` 命令时，maven 会清除 target 下的所有文件，从而使 JPress 会再次走安装流程。




## 微信交流群

![](./doc/images/jpress-wechat-group.png)




## Contributors

### Code Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.md)].
<a href="https://github.com/JPressProjects/jpress/graphs/contributors"><img src="https://opencollective.com/jpress/contributors.svg?width=890&button=false" /></a>

### Financial Contributors

Become a financial contributor and help us sustain our community. [[Contribute](https://opencollective.com/jpress/contribute)]

#### Individuals

<a href="https://opencollective.com/jpress"><img src="https://opencollective.com/jpress/individuals.svg?width=890"></a>

#### Organizations

Support this project with your organization. Your logo will show up here with a link to your website. [[Contribute](https://opencollective.com/jpress/contribute)]

<a href="https://opencollective.com/jpress/organization/0/website"><img src="https://opencollective.com/jpress/organization/0/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/1/website"><img src="https://opencollective.com/jpress/organization/1/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/2/website"><img src="https://opencollective.com/jpress/organization/2/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/3/website"><img src="https://opencollective.com/jpress/organization/3/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/4/website"><img src="https://opencollective.com/jpress/organization/4/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/5/website"><img src="https://opencollective.com/jpress/organization/5/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/6/website"><img src="https://opencollective.com/jpress/organization/6/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/7/website"><img src="https://opencollective.com/jpress/organization/7/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/8/website"><img src="https://opencollective.com/jpress/organization/8/avatar.svg"></a>
<a href="https://opencollective.com/jpress/organization/9/website"><img src="https://opencollective.com/jpress/organization/9/avatar.svg"></a>

![](./doc/assets/images/commons/screenshot.png)


<h1 align="center"><a href="http://www.jpress.cn" target="_blank"> JPress </a></h1>

<p align="center">
一个使用 Java 开发的、类似 WordPress 的产品，支持多站点、多语种自动切换等。（JPress 始于2015 年）
</p>


## 功能

#### 文章模块
- 文章管理
- 文章分类
- 文章标签
- 文章搜索（支持 sql like、Lucene、es、OpenSearch）

#### 页面模块
- 页面管理
- 页面分类
- 页面评论

#### 招聘模块
- 职位管理
- 简历管理
- 职位分类
- 招聘设置

#### 商品模块
- 商品管理
- 商品分类
- 商品评论
- 商品设置

#### 表单模块
- 表单管理
- 表单拖拽设计
- 表单插入到文章
- 表单数据收集
- 表单数据统计

#### 附件管理
- 附件列表
- 插入附件到文章、页面
- 附件分类
- 视频附件


#### 用户相关
- 用户管理
- 权限管理
- 用户标签
- 短信群发
- 邮件群发


#### 系统相关
- 模板管理
- 插件管理
- 微信管理
- 系统管理
- 站点管理


## 特点

#### 模板

- 模板在线安装、卸载
- 模板在线启用、切换
- 在线编辑及实时生效
- 模板模的块拖拽设计
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
- 用户中心（评论管理、个人资料管理等）
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




#### 多站点

- 支持站点绑定独立域名
- 支持站点绑定独立二级目录
- 支持站点绑定不同地区语言
- 支持访问主站是自动根据语言跳转到子站点


#### SEO

- 每篇文章、页面和商品独立的SEO设置
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

- 官网：[http://www.jpress.cn](http://www.jpress.cn)
- 插件列表：[点击这里](http://www.jpress.cn/article/category/plugin)
- 模板列表：[点击这里](http://www.jpress.cn/article/category/template)
- QQ群：591396171 ，288397536


## 帮助文档

- [用户手册](http://doc.jpress.cn/manual/)
- [开发文档](http://doc.jpress.cn/development/)
- [模板开发](http://doc.jpress.cn/development/template/start.html)
- [插件开发](http://doc.jpress.cn/development/addon/start.html)
- [二次开发](hhttp://doc.jpress.cn/development/dev/start.html)
- [API 接口](http://doc.jpress.cn/development/api/start.html)
- [视频教程](http://www.ketang8.com/course/5)
- [JPress-VIP 会员](http://www.jpress.cn/article/vip)


## 广告

- 一个好用的在线代码格式化工具：[http://www.CodeFormat.CN](http://www.codeformat.cn)


## 运行JPress


**在 Docker 上运行**

```
curl -O https://gitee.com/JPressProjects/jpress/raw/master/docker-compose.yml && \
docker-compose up -d
```

**在阿里云（或腾讯云）一键通过 80 端口安装运行**

```
wget https://gitee.com/JPressProjects/jpress/raw/master/install.sh && \
bash install.sh 80
```

> 一键安装的视频教程：[http://www.ketang8.com/course/study?chapterId=184](http://www.ketang8.com/course/study?chapterId=184)


**通过 Eclipse 或者 Idea 等开发工具运行**

- 1、在电脑安装好 Java、Maven 等开发环境
- 2、将源码下载、并导入 eclipse 或者 idea 
- 3、在项目的**根目录**，执行 `mvn clean package` 命令进行编译
- 4、在开发工具，右键运行 `starter/src/main/java/io.jpress.Starter` 下的 `main` 方法
- 5、通过浏览器访问 `http://127.0.0.1:8080`，进行自动安装，安装的过程会自动建库建表

> JPress下载、导入、运行视频教程，链接: https://pan.baidu.com/s/1bqbQ9_HjF95EW4qrQvOSag 提取码: 5jw8 


**注意！注意！注意！JPress 首次运行后，若再执行 `mvn clean package` 命令，JPress会重新走安装流程。**
>
> 解决方案： JPress 在安装过程中，会在 `starter/target/classes` 目录下生成的 `jboot.properties` 和 `install.lock` 两个文件，
> 我们需要把这两个文件复制到 `starter/src/main/resource` 目录。 
> 
> 原因是：JPress 是否安装，决定在这两个文件。有这两个文件，JPress 就不走安装流程，没有就走安装流程。 当我们执行 `mvn clean` 命令时，
> Maven 会清除 `starter/target` 目录下的所有文件，从而使这两个文件丢失， JPress 会再次走安装流程。
> 只有把这两个文件， 复制到 `starter/src/main/resource` 目录， 再次执行 `mvn clean package` 命令时，才能保证这两个文件不会丢失，不走安装流程。




## JPress 交流群

- QQ 群1：591396171
- QQ 群2：288397536


微信交流群：

![](./doc/assets/images/commons/wechat-group.png)

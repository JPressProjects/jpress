# JPress 的代码架构图和目录架构



#### JPress 架构图

![](../image/module/module_27.png)

#### JPress 目录结构

| 目录  |   备注 |
| --- |  --- |
| codegen | 代码生成器，开发的时候用与生成maven模块代码，运行时用不到该模块 |
| doc | 文档存放目录  |
| jpress-commons | 工具类和公用代码   |
| jpress-core | JPress的核心代码  |
| jpress-model | JPress非业务实体类  |
| jpress-service | JPress非业务 service 接口定义   |
| jpress-service-provider | JPress非业务 service 接口实现  |
| jpress-template | JPress的html模板   |
| jpress-web | JPress非业务的web处理代码，包含了 Controller、指令等 |
| module-article | 文章模块代码   |
| module-form | 表单模块代码   |
| module-job | 招聘模块代码   |
| module-page | 页面模块代码  |
| module-product | 商品模块代码  |
| starter | undertow启动模块，开发的时候可以运行里面的 Starter.java 的main方法，编译的时候会生成 jpress 可执行程序 |
| starter-tomcat | tomcat 启动模块，编译的时候会生成 war 包，用于放在tomcat部署 |

# 插件开发

开发插件，和开发一个 Maven Module 没有什么区别，需要注意的是 JPress 插件有自己独立的配置文件。

## 插件的相关文件

- addon.txt ：插件的声明文件
- config.txt ： 插件的配置文件

## addon.txt

```
id=io.jpress.addon.helloworld
title=hello world
description=这只是一个插件demo，方便开发者参考开发
author=海哥
authorWebsite=
version=
versionCode=1
```

- id：插件的唯一ID，在整个 JPress 应用中，不允许两个ID一样的插件同时存在。
- title： 插件的名称，这个名称会在后台的插件列表里显示
- description：插件的描述
- author：插件的作者
- authorWebsite：插件作者的官网，或者插件的官网
- version：插件的版本号，可以是字符串，例如： v1.0.0
- versionCode：插件的版本号码，必须是数字类型，以后插件的升级检测会通过 versionCode 进行验证，而非 version

## config.txt

```
db.type = mysql
db.url
db.user
db.password
db.driverClassName = "com.mysql.jdbc.Driver"
db.connectionInitSql
db.poolName
db.cachePrepStmts = true
db.prepStmtCacheSize = 500
db.prepStmtCacheSqlLimit = 2048
db.maximumPoolSize = 10
db.maxLifetime
db.idleTimeout
db.minimumIdle = 0
db.sqlTemplatePath
db.sqlTemplate
db.factory
db.shardingConfigYaml
db.dbProFactory
db.containerFactory
db.transactionLevel
db.table #此数据源包含哪些表，这个配置会覆盖@Table注解的配置
db.exTable #该数据源排除哪些表，这个配置会修改掉@Table上的配置
db.dialectClass
db.activeRecordPluginClass
```



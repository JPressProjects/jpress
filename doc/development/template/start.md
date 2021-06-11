# 模板简介

在开发JPress模板之前，现做好JPress的环境搭建，前端开发人员建议用 Docker 的方式，可以免除 Java 等环境的安装和配置。

通过 Docker 安装 JPress 请参考：[《Docker下安装启动》](../../manual/install_docker.md)，其他请参考：
- [《Windows下安装启动》](../../manual/install_windows.md)
- [《Linux下安装启动》](../../manual/install_linux.md)
- [《Mac下安装启动》](../../manual/install_mac.md)

## 模板的组成

一个完整的模板，文件内容如下：

```
mytemplate
├── index.html
├── screenshot.png
└── template.properties
```

其中 `index.html` 、 `screenshot.png` 、`template.properties` 是模板的必须文件，一个模板最少由着三个文件组成。
> 更多文件请参考: [模板的目录结构](./structure.md)

- index.html : 网站首页的模板
- screenshot.png : 后台的模板缩略图
- template.properties 模板的配置信息

::: tip 提示
当我们开始开发一个新的模板的时候，可以先用这个三个文件，打包成 zip 包之后，通过 JPress 后台进行安装，看一下效果。同时可以把 `mytemplate` 文件夹直接复制到 JPress 的 `templates` 目录下也等同于安装。
:::

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




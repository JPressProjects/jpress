# 模板开发

## 目录

- 模板开发准备
- 模板的组成
- 模板的目录结构
- 模板的多样式
- 模板的手机版样式
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
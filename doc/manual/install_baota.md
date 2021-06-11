# 在 宝塔 上安装 JPress


宝塔是一个优秀的可视化服务器管理工具，提供了web操作面板，方便我们通过宝塔的web面板对服务器进行管理，例如

1、数据库安装、账号密码管理和数据管理 2、FTP账号的管理 3、各种服务器软件的安装，php、tomcat、nginx等 4、文件管理

宝塔的官网网站： http://www.bt.cn

通过宝塔进行安装 JPress，大概分为以下几个步骤：

[[toc]]

## 1、购买服务器并安装宝塔

购买服务器建议购买阿里云的 centos 7.4 以上 ，里面不要安装其他任何功能（笔者在centos 7.2 下安装宝塔，nginx是无法使用的，centos 7.4 没问题）

安装宝塔非常简单，用 root 账号进入Linux服务，然后执行如下命令即可自动安装宝塔：

```shell
yum install -y wget 
wget -O install.sh http://download.bt.cn/install/install_6.0.sh && bash install.sh
```

需要注意的是：安装的过程中控制台会打印安装的过程，在安装完成后，控制台会输出宝塔的登陆地址、账号和密码。账号和密码请务必牢记。



## 2、通过宝塔的后台，安装 Nginx、Mysql 和 Tomcat

可以在宝塔的后台，通过软件管理 > 运行环境可以找到 nginx、mysql 和 tomcat。

点击安装即可。

需要注意的是各个软件的版本号：

- nginx ： 1.14
- tomcat ： 8.5
- mysql ： 5.7

## 3、创建网站

在宝塔后台，通网站 > 添加网站创建一个新的网站。

创建网站的时候需要注意的是，创建mysql数据库的时候，版本要选择 utf8mb4 编码。

在宝塔后台的网站里，点击网站域名，在 tomcat 菜单里，启用 tomcat 功能。

## 4、上传war，并解压缩

在宝塔后台的网站里，点击根目录对应的目录链接，然后上传 jpress.war 到此目录。

因为宝塔无法对 .war 这种文件格式解压缩，所以需要重命名为 jpress.zip ，当然也可以在本地先把 jpress.war 先重命名为 jpress.zip 然后再上传也可以。

操作完成后，点击 jpress.zip 的解压缩即可。

## 5、访问网站走 JPress 自动安装过程
访问你的域名，JPress 自动引导进行安装，在 JPress 安装向导的过程中，只需要填写宝塔创建完毕的数据库账号和密码即可。
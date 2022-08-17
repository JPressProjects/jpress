## Tomcat 下载安装及配置

### 1、Tomcat下载

下载tomcat 9，官网地址[https://tomcat.apache.org/](https://tomcat.apache.org/)

![](tomcat_img/img.png)

点击选择 tomcat 9 进入下载页面，再点击下载 64-Bit Windows zip（Win64）

![](tomcat_img/img_1.png)

找到下载的压缩包并解压

![](tomcat_img/img_2.png)
    
### 2、Tomcat 配置环境变量

首先在电脑左下角搜索框搜索控制面板并打开。

![](jdk_image/jdk_8.png)

之后打开系统与安全面板下面的系统菜单。

![](jdk_image/jdk_9.png)

接着打开高级系统设置弹窗里面的高级-环境变量即可。

![](jdk_image/jdk_10.png)

新建环境变量，变量名为 TOMCAT_HOME,变量值为 tomcat 解压后所在的路径，点击确定

![](tomcat_img/img_4.png)

双击 Path，进入界面后，点击编辑文本

![](tomcat_img/img_5.png)

### 3、验证是否配置成功

按win+r 快捷键 打开命令窗口，输入 cmd 指令，点击确定

![](jdk_image/img.png)

输入启动指令：

    startup.bat

![](tomcat_img/img_6.png)

上图启动 tomcat 后乱码的问题

打开解压后的文件：apache-tomcat-9.0.65->conf->logging.properties

![](tomcat_img/img_7.png)

打开 logging.properties，找到代码：

    java.util.logging.ConsoleHandler.encoding = UTF-8

![](tomcat_img/img_8.png)

修改为：

    java.util.logging.ConsoleHandler.encoding = GBK

![](tomcat_img/img_9.png)

重新启动 tomcat，输入指令 startup.bat，乱码问题得到解决。

![](tomcat_img/img_10.png)

验证是否配置成功 

![](tomcat_img/img_11.png)

注：上图测试时，下图输入指令 startup.bat 启动的 tomcat 页面不能关闭，否则会报错

![](tomcat_img/img_12.png)

tomcat 默认端口号为 8080，若 8080 被占用或者想更换访问端口

编辑文件：apache-tomcat-9.0.65 -> conf -> server.xml

![](tomcat_img/img_13.png)

打开 server.xml，找到 Connector 标签所在位置，如下图所示

![](tomcat_img/img_14.png)

端口号修改

![](tomcat_img/img_15.png)

重新启动 tomcat

![](tomcat_img/img_16.png)

测试

![](tomcat_img/img_17.png)



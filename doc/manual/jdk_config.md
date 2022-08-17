## jdk 下载安装及配置

jdk 官网地址： [https://www.oracle.com/java/](https://www.oracle.com/java/)

### 1、jdk 下载

进入官网，定位到：Java -> Java SE -> Oracle JDK 点击进入，如下图所示：

![jdk_1](./jdk_image/jdk_1.png)

选择 Java archive，再鼠标下拉页面，选择 Java SE 8 (8u202 and earlier)

![jdk_2](./jdk_image/jdk_2.png)

下载 jdk-8u202-windows-x64.exe

![jdk_3](./jdk_image/jdk_3.png)

![](./jdk_image/jdk_7.png)

### 2、jdk 安装


下载 jdk 到本地，找到该文件，鼠标双击 .exe 应用程序，运行 jdk 进行安装

![](./jdk_image/jdk_4.png)

进入 jdk 安装界面，点击下一步

![](./jdk_image/jdk_5.png)

安装位置可以默认，也可以自定义，然后一直点击下一步直到完成即可。

![](./jdk_image/jdk_6.png)

### 3、配置 jdk1.8 的环境变量

在电脑左下角搜索框输入：控制面板

![](./jdk_image/jdk_8.png)

控制面板 -> 系统与安全 -> 系统

![](./jdk_image/jdk_9.png)

高级系统设置 -> 高级 -> 环境变量

![](./jdk_image/jdk_10.png)

新建环境变量，变量名为 JAVA_HOME，变量值为 jdk 安装的路径

![](./jdk_image/jdk_11.png)

鼠标双击 Path，已安装过 jdk 的点击编辑，第一次安装的点击新建

![](./jdk_image/jdk_12.png)

### 4、验证 jdk 是否配置成功

按 win+r 快捷键，打开命令窗口，输入 cmd 指令，点击确定

![](./jdk_image/img.png)

输入指令：

    java -version

若能查看到安装的 jdk 版本，则配置成功

![](./jdk_image/img_1.png)


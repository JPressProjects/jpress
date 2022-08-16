# Windows 上实现 undertow 部署

## 部署环境：jdk1.8

## Oracle官网
Oracle官网地址 [https://www.oracle.com/java/](https://www.oracle.com/java/)

### 1、JDK 下载

    1.1 鼠标下拉定位到 Java，选择 Oracle JDK，如下图所示：

![jdk_1](jdk_image/jdk_1.png)

    1.2 选择 Java archive，再鼠标下拉，选择 Java SE 8 (8u202 and earlier)

![jdk_2](jdk_image/jdk_2.png)

    1.3 下载 jdk-8u202-windows-x64.exe

![jdk_3](jdk_image/jdk_3.png)

![](jdk_image/jdk_7.png)

### 2、JDK 安装


    2.1 下载 JDK 到本地，找到 JDK 所在文件夹，双击 .exe 运行 JDK ，进行程序安装

![](jdk_image/jdk_4.png)

    2.2 进入 JDK 安装界面，点击下一步

![](jdk_image/jdk_5.png)


    2.3 安装位置可以默认，也可以自定义，然后一直点击下一步直到完成即可。

![](jdk_image/jdk_6.png)

### 3、配置环境变量

    3.1 首先在电脑左下角搜索框输入：控制面板

![](jdk_image/jdk_8.png)

    3.2 控制面板 -> 系统与安全 -> 系统

![](jdk_image/jdk_9.png)

    3.3 高级系统设置 -> 高级 -> 环境变量

![](jdk_image/jdk_10.png)

    3.4 新建环境变量，变量名为 JAVA_HOME，变量值为 JDK 的安装路径

![](jdk_image/jdk_11.png)

    3.5  双击 Path，编辑环境变量，点击新建

![](jdk_image/jdk_12.png)

### 4、 验证 jdk 是否配置成功


    4.1 按 win+r 快捷键，打开命令窗口，输入 cmd 指令，点击确定

![](jdk_image/img.png)

    4.2 输入指令：java -version，可以查看到安装的 jdk 版本则成功。

![](jdk_image/img_1.png)

## undertow 部署项目

### 1、部署

    1.1 项目打包，输入指令：mvn clean package

![](windows_undertow_img/img_15.png)

    1.2 项目打包后生成的压缩包：start -> target -> starter-4.0.zip

![](windows_undertow_img/img.png)

    1.3 找到压缩包在 Windows 的位置：
        鼠标选中压缩包，单击右键选择 Open In -> Explorer，
        如下图所示：

![](windows_undertow_img/img_1.png)

    1.4 新建文件夹存放压缩包，并解压缩

![](windows_undertow_img/img_2.png)

    1.4 进入解压后的文件夹

![](windows_undertow_img/img_3.png)

    1.5 输入 cmd 指令，按下回车键，进入命令界面，如下三图所示：

![](windows_undertow_img/img_4.png)

![](windows_undertow_img/img_5.png)

![](windows_undertow_img/img_6.png)

    1.6 启动项目，输入指令：jpress.bat start，按下回车键
        关闭界面，项目会停止

![](windows_undertow_img/img_7.png)

    1.7 配置后台启动，输入指令：start /b  jpress.bat start
        这个后台启动指的是，你可以继续在命令界面进行其他操作
        关闭界面，项目会停止

![](windows_undertow_img/img_8.png)

    1.8 上图项目启动完成，进行浏览器访问，如下图所示：

![](windows_undertow_img/img_9.png)

    1.9 要进行接下来的操作，
        Windows 要安装 mysql 5.7 数据库，MySQL 下载安装（略）

![](windows_undertow_img/img_10.png)

    1.10 配置后台启动，关闭命令界面，项目照常运行
        找到解压缩包下的文件：jpress-start.bat

![](windows_undertow_img/img_11.png)

    1.11 鼠标选中 jpress-start.bat 单击右键，选择编辑，进入编辑页面
        添加以下代码，保存关闭：

        @echo off
        if "%1"=="h" goto begin
        start mshta vbscript:createobject("wscript.shell").run("""%~nx0"" h",0)(window.close)&&exit
        :begin

        如下图所示：

![](windows_undertow_img/img_12.png)

    1.12 鼠标双击 jpress-start.bat 启动项目
         鼠标双击 jpress-stop.bat 停止项目

![](windows_undertow_img/img_13.png)

    1.13 配置启动项目后，打开浏览器进入指定网址页面，如下图所示：
        编辑 jpress-start.bat 文件
        输入：start ChromeCore.exe -k "http://localhost:8080" 

        ChromeCore.exe：Windows 安装的浏览器程序
        http://localhost:8080 ：指定的网址

![](windows_undertow_img/img_14.png) 

    双击 jpress-start.bat 启动项目后，自动打开浏览器

![](windows_undertow_img/img_9.png)





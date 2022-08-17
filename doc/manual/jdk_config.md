# JDK 下载安装及配置

## 进入 Oracle官网 的 Java 界面
Oracle官网地址 [https://www.oracle.com/java/](https://www.oracle.com/java/)

### 1、JDK 下载

    1.1 在网站页面滚动鼠标下拉定位到 Java，选择 Oracle JDK

![jdk_1](./jdk_image/jdk_1.png)

    1.2 选择 Java archive，再滚动鼠标下拉，选择 Java SE 8 (8u202 and earlier)

![jdk_2](./jdk_image/jdk_2.png)

    1.3 选择你需要下载的 jdk-8u202-windows-x64.exe

![jdk_3](./jdk_image/jdk_3.png)

![](./jdk_image/jdk_7.png)

### 2、JDK安装


    2.1 下载完 JDK 到本地后，找到该文件，双击运行 JDK 安装程序

![](./jdk_image/jdk_4.png)

    2.2 等待一会，进入 JDK 安装界面，点击下一步

![](./jdk_image/jdk_5.png)


    2.3 安装位置可以默认，也可以选择自己想要存放的位置，然后一直点击下一步直到完成即可。

![](./jdk_image/jdk_6.png)

### 3、配置环境变量

    3.1 首先在电脑左下角搜索框搜索控制面板并打开。

![](./jdk_image/jdk_8.png)

    3.2 之后打开系统与安全面板下面的系统菜单。

![](./jdk_image/jdk_9.png)

    3.3 接着打开高级系统设置弹窗里面的高级-环境变量即可。

![](./jdk_image/jdk_10.png)

    3.4 新建环境变量，变量名为 JAVA_HOME,变量值为 JDK 安装的路径，点击确定

![](./jdk_image/jdk_11.png)

    3.5  双击 Path，进入界面后，点击编辑文本

![](./jdk_image/jdk_12.png)

### 4、 验证 jdk 是否配置成功


    4.1 按win+r 快捷键 打开命令窗口，输入cmd指令，点击确定

![](./jdk_image/img.png)

    4.2 输入指令 java -version，可以查看到安装的jdk版本则成功。

![](./jdk_image/img_1.png)

### 5、 在 IDEA 中配置 jdk


    5.1 setting 是 idea 配置，Project Structure 是项目配置

![](./jdk_image/img_2.png)

    5.2 点击 Project Structure 进入项目配置，选择你安装的 jdk,再点击 apply应用

![](./jdk_image/img_3.png)

    5.3 点击 Modules,选择安装的 jdk,再点击应用

![](./jdk_image/img_4.png)

    5.4 SDKs 的配置，选择你安装的 jdk

![](./jdk_image/img_5.png)
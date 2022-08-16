## Tomcat下载安装及配置

###1.Tomcat下载

    1.1 选择你要下载的版本，官网地址[https://tomcat.apache.org/](https://tomcat.apache.org/)
        此处以tomcat 9示例

![](tomcat_img/img.png)

    1.2 点击选择tomcat 9进入下载页面，再点击下载64-Bit Windows zip（Win64）

![](tomcat_img/img_1.png)

    1.3 找到下载的压缩包，并解压
        可选个磁盘自定义一个文件夹来放置，解压文件所在的路径配置环境变量时会用到

![](tomcat_img/img_2.png)
    
###2.Tomcat 配置环境变量

    2.1 首先在电脑左下角搜索框搜索控制面板并打开。

![](jdk_image/jdk_8.png)

    2.2 之后打开系统与安全面板下面的系统菜单。

![](jdk_image/jdk_9.png)

    2.3 接着打开高级系统设置弹窗里面的高级-环境变量即可。

![](jdk_image/jdk_10.png)

    2.4 新建环境变量，变量名为TOMCAT_HOME,变量值为tomcat解压后所在的路径，点击确定

![](tomcat_img/img_4.png)

    2.5 双击path，进入界面后，点击编辑文本

![](tomcat_img/img_5.png)

###3.验证是否配置成功

    3.1 按win+r 快捷键 打开命令窗口，输入cmd指令，点击确定

![](jdk_image/img.png)

    3.2 输入指令 startup.bat，看能否正常启动

![](tomcat_img/img_6.png)

    3.3  上图启动tomcat后乱码的问题
        打开解压后的文件apache-tomcat-9.0.65->conf->logging.properties

![](tomcat_img/img_7.png)

    3.4 打开logging.properties
        找到这一行代码：java.util.logging.ConsoleHandler.encoding = UTF-8

![](tomcat_img/img_8.png)

    3.5 修改为：java.util.logging.ConsoleHandler.encoding = GBK

![](tomcat_img/img_9.png)

    3.6 再重新启动tomcat，输入指令 startup.bat，乱码问题得到解决。

![](tomcat_img/img_10.png)

    3.7 验证是否配置成功 

![](tomcat_img/img_11.png)

    注：上图测试时，下图输入指令 startup.bat 启动的tomcat页面不能关闭，否则会报错

![](tomcat_img/img_12.png)

    3.8 tomcat默认端口号为8080，若8080被占用或者你想换一个端口号
        找到apache-tomcat-9.0.65->conf->server.xml

![](tomcat_img/img_13.png)

    3.9 打开server.xml，找到 Connector 标签所在位置，如下图所示

![](tomcat_img/img_14.png)

    3.10 端口号修改

![](tomcat_img/img_15.png)

    重新启动tomcat

![](tomcat_img/img_16.png)

    测试

![](tomcat_img/img_17.png)

###4. IDEA 配置tomcat


    4.1 第一步打开idea，点击Run->EDit Configurations

![](tomcat_img/img_3.png)

    4.2 进去之后，点击 + 号，下拉选择Tomcat Server -> local

![](tomcat_img/img_18.png)

    4.3 选择你下载的tomcat解压后的路径，

![](tomcat_img/img_19.png)

    4.4 再点击添加要部署的war包,再点击apply应用

![](tomcat_img/img_22.png)

![](tomcat_img/img_20.png)

![](tomcat_img/img_23.png)

    配置完Deployment,再来看server,URL地址已经被修改

![](tomcat_img/img_24.png)

    4.5 IDEA启动配置的tomcat 9

![](tomcat_img/img_21.png)

    启动成功，但控制台出现中午乱码问题

![](tomcat_img/img_25.png)

    4.6 打开apache-tomcat-9.0.65->conf->logging.properties
        找到下图所示内容注释掉，并在下面重新添加把 UTF-8 修改为 GBK

![](tomcat_img/img_26.png)

        重新启动tomcat,控制台乱码问题解决    

![](tomcat_img/img_27.png)

    4.7 tomcat部署启动成功

![](tomcat_img/img_28.png)

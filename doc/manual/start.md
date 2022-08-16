# 快速开始


[[toc]]

## 准备

- 安装 Java 环境
- 安装 maven 编译环境

**安装Java环境**

点击查看:[java环境配置](jdk_config.md)，通过引导即可进行安装配置。


**安装 maven 编译环境**

点击查看:[maven环境配置](maven_config.md)，通过引导即可进行安装配置。

## 下载

```
git clone https://gitee.com/JPressProjects/jpress.git
```

## 编译

```
cd jpress
mvn clean package
```

## 运行

```
cd starter/target/starter-4.0
./jpress.sh start
```

## 停止

```
./jpress stop
```

## 重启

```
./jpress restart
```
# JPress 升级

## 目录
- Tomcat 下升级 JPress
- Docker 下升级 JPress


## Tomcat 下升级 JPress

**第一步**

下载最新的 war 包到 Linux 上，并解压缩到任意目录，假设解压缩到 `/tmp/jpress` 。

**第二步**

下载 upgrade.sh 到 Linux 上，并给与执行权限。

```shell script
wget https://gitee.com/fuhai/jpress/raw/master/upgrade.sh
chmod +x upgrade.sh
```

**第三步**

执行 upgrade.sh，假设要升级的旧的JPress所在的目录为 `/www/tomcat/webapps/jpress`，需要执行
如下代码

```shell script
./upgrade.sh /tmp/jpress /www/tomcat/webapps/jpress
```

>upgrade.sh 后面跟两个参数，第一个为新的 war 包解压的目录，第二个参数为需要升级的旧的JPress所在目录。

**第四步**

重启 Tomcat，升级完成。


## Docker 下升级 JPress

Docker 一般情况是通过 docker-compose.yml 文件来进行部署，升级 JPress 只需要修改 
docker-compose.yml 里的 JPress 的版本号。然后执行如下命令。

```shell script
docker-compose down
docker-compose up -d
```


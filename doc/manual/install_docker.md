# 在 Docker 上安装 JPress

通过 Docker 进行安装 JPress，需要提前在本地安装好 Docker。

## Docker 的安装请参考官方文档：


* [Mac](https://docs.docker.com/docker-for-mac/install)
* [Windows](https://docs.docker.com/docker-for-windows/install)
* [Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu)
* [Debian](https://docs.docker.com/install/linux/docker-ce/debian)
* [CentOS](https://docs.docker.com/install/linux/docker-ce/centos)
* [Fedora](https://docs.docker.com/install/linux/docker-ce/fedora)
* [其他 Linux 发行版](https://docs.docker.com/install/linux/docker-ce/binaries)


## 下载 docker-compose.yml 配置文件

Linux 上：

```shell
wget https://gitee.com/JPressProjects/jpress/raw/master/docker-compose.yml
```

 Mac OS（苹果电脑）上：

```shell
curl -O https://gitee.com/JPressProjects/jpress/raw/master/docker-compose.yml
```

## 通过 Docker-Compose 启动 JPress

```shell
docker-compose up -d
```

执行完毕以上命令之后，访问 `http://127.0.0.1:8080` 即可。

在很多场景下，我们可能还需要对 JPress 进行启动、停止、重启 和 卸载 等操作。可以参考以下命令：

停止 JPress

```shell
docker-compose stop
```

启动 JPress

```shell
docker-compose start
```

重启 JPress

```shell
docker-compose restart
```

卸载 JPress

```shell
docker-compose down
```


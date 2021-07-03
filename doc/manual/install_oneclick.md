# 一键安装 JPress

目前只支持在 Linux 操作系统上一键安装 JPress，当我们购买了全新的阿里云、腾讯云等服务器，我们执行如下脚本进行一键安装。

```shell
wget https://gitee.com/JPressProjects/jpress/raw/master/install.sh && bash install.sh 80
```

安装完成后，需要访问 `http://ip地址` 即可访问到 JPress，并走自动安装流程。


一键安装视频教程：[http://www.ketang8.com/course/study?chapterId=184](http://www.ketang8.com/course/study?chapterId=184)


::: tip 提示
`install.sh` 后边的参数是端口号，不输入默认为 8080。若在安装的过程中有错误（一般情况都是网络错误），一般只需要再执行一遍即可。
:::


## 脚本执行原理


此脚本的执行原理是：第一先检查 Linux 上是否已经安装有 Docker，如果没有安装，则帮助用户安装 Docker，
若服务器已经安装，则跳过 Docker 的安装过程，Docker 安装完成后
，接下来会下载 JPress 的 `docker-compose.yml` 到服务器上。并通过 Docker 启动 JPress。

所以，只要此脚本执行完毕后，会在当前目录下 生成名称为 `docker-compose.yml` 的文件。

默认情况下，JPress 服务的端口号是在 `docker-compose.yml` 文件里定义的，如果我们想让 `http://ip地址`（不添加端口号） 可以访问
到 JPress，则需要修改 `docker-compose.yml` 里的端口号。然后通过 `docker-compose` 命令重启 JPress。

> docker-compose 的启动命令请参考 [这里](./install_docker)

例如：

```yaml {24}
version: '3.1'

services:

  db:
    image: mysql:5.7
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: jpress
      MYSQL_DATABASE: jpress
      MYSQL_USER: jpress
      MYSQL_PASSWORD: jpress
    volumes:
      - "./docker_volumes/mysql:/var/lib/mysql"

  jpress:
    depends_on:
      - db
    links:
      - db
    image: fuhai/jpress:v3.3.0
    ports:
      - "8080:8080"
    restart: always
    environment:
      TZ: Asia/Shanghai
      JPRESS_DB_HOST: db
      JPRESS_DB_PORT: 3306
      JPRESS_DB_NAME: jpress
      JPRESS_DB_USER: jpress
      JPRESS_DB_PASSWORD: jpress
    volumes:
      - "./docker_volumes/webapp/attachment:/opt/jpress/webapp/attachment"
      - "./docker_volumes/webapp/addons:/opt/jpress/webapp/addons"
      - "./docker_volumes/webapp/WEB-INF/addons:/opt/jpress/webapp/WEB-INF/addons"
      - "./docker_volumes/webapp/wp-content:/opt/jpress/webapp/wp-content"
      - "./docker_volumes/webapp/templates/dockers:/opt/jpress/webapp/templates/dockers"
```

需要修改以上配置 `"8080:8080"` 为 `"80:8080""`例如：

```yaml  {24}
version: '3.1'

services:

  db:
    image: mysql:5.7
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: jpress
      MYSQL_DATABASE: jpress
      MYSQL_USER: jpress
      MYSQL_PASSWORD: jpress
    volumes:
      - "./docker_volumes/mysql:/var/lib/mysql"

  jpress:
    depends_on:
      - db
    links:
      - db
    image: fuhai/jpress:v4.0.2
    ports:
      - "80:8080"
    restart: always
    environment:
      TZ: Asia/Shanghai
      JPRESS_DB_HOST: db
      JPRESS_DB_PORT: 3306
      JPRESS_DB_NAME: jpress
      JPRESS_DB_USER: jpress
      JPRESS_DB_PASSWORD: jpress
    volumes:
      - "./docker_volumes/webapp/attachment:/opt/jpress/webapp/attachment"
      - "./docker_volumes/webapp/addons:/opt/jpress/webapp/addons"
      - "./docker_volumes/webapp/WEB-INF/addons:/opt/jpress/webapp/WEB-INF/addons"
      - "./docker_volumes/webapp/wp-content:/opt/jpress/webapp/wp-content"
      - "./docker_volumes/webapp/templates/dockers:/opt/jpress/webapp/templates/dockers"
```
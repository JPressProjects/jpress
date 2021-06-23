# JPress 升级


[[toc]]


## Tomcat 下升级 JPress

**第一步**

下载最新的 war 包到 Linux 上，并解压缩到任意目录，假设解压缩到 `/tmp/jpress` 。

**第二步**

下载 upgrade.sh 到 Linux 上，并给与执行权限。

```shell script
wget https://gitee.com/JPressProjects/jpress/raw/master/upgrade.sh
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

Docker 升级（或降级）JPress，都是通过修改 `docker-compose.yml` 文件里的 JPress 版本号。 然后执行如下命令。

```shell script
docker-compose down
docker-compose up -d
```

例如：

```yaml {22}
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

需要修改 `fuhai/jpress:v3.3.0` 的版本号，例如：

```yaml  {22}
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
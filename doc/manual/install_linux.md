# 在 Linux 上安装 JPress


[[toc]]

通过 Maven 编译 JPress 成功之后，会在 `starter/target` 目录下生成 `starter-4.0` 的目录。
该目录的内容如下：


```
├── config
├── jpress-start.bat
├── jpress-stop.bat
├── jpress.bat
├── jpress.sh
├── lib
└── webapp
```

> 如何编辑 JPress，请参考 [这里](../development/dev/start)

启动 JPress 执行如下命令：

```shell
./jpress.sh start
```

后台启动 JPress 需要修改 `jpress.sh` 文件里的启动脚本。找到 `start()` 方法如下图：

```shell
function start()
{
    java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} &
}
```
将其修改内容为如下所示：

```shell
function start()
{
    nohup java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} >/dev/null 2>&1 &
}
```


停止 JPress
```shell
./jpress.sh stop
```

重启 JPress
```shell
./jpress.sh restart
```
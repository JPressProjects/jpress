# 在 Windows 上安装 JPress

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
jpress.bat start
```
> 或者直接双击：`jpress-start.bat` 文件即可启动。

后台启动 JPress
```shell
start /b  jpress.bat start
```

停止 JPress
```shell
jpress.bat stop
```
> 或者直接双击：`jpress-stop.bat` 文件即可停止。

重启 JPress
```shell
jpress.bat restart
```
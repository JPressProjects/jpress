#!/bin/bash
# ----------------------------------------------------------------------
# name:         jpress.sh
# version:      1.0
# description:  JPress 控制脚本
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : ./jpress.sh {start, stop, restart}
# ----------------------------------------------------------------------

# 启动入口类，该脚本文件用于别的项目时要改这里
MAIN_CLASS=io.jboot.app.JbootApplication
COMMAND="$1"

if [[ "$COMMAND" != "start" ]] && [[ "$COMMAND" != "stop" ]] && [[ "$COMMAND" != "restart" ]]; then
	echo "./jpress.sh {start, stop, restart}"
	exit 0
fi

# Java 命令行参数，根据需要开启下面的配置，改成自己需要的，注意等号前后不能有空格
# JAVA_OPTS="-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
# JAVA_OPTS="-Dundertow.port=8080 -Dundertow.host=0.0.0.0"

# 生成 class path 值
APP_BASE_PATH=$(cd `dirname $0`; pwd)
CP=${APP_BASE_PATH}/config:${APP_BASE_PATH}/lib/*

function start()
{
    java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS}
}

function stop()
{
    kill `pgrep -f ${MAIN_CLASS}` 2>/dev/null

}

if [[ "$COMMAND" == "start" ]]; then
	start
elif [[ "$COMMAND" == "stop" ]]; then
    stop
else
    stop
    start
fi

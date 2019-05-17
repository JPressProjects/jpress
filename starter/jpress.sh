#!/bin/bash
# ----------------------------------------------------------------------
# name:         jpress.sh
# version:      1.0
# description:  JPress 控制脚本
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : ./jpress.sh {start, stop, restart}
# ----------------------------------------------------------------------

MAIN_CLASS=io.jpress.Starter
COMMAND="$1"

if [[ "$COMMAND" != "start" ]] && [[ "$COMMAND" != "stop" ]] && [[ "$COMMAND" != "restart" ]]; then
	echo "Usage:  ./jpress.sh {start, stop, restart}"
	exit 0
fi



# Java 命令行参数，根据需要开启下面的配置，改成自己需要的，注意等号前后不能有空格
# JAVA_OPTS="-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
# JAVA_OPTS="-Dundertow.port=80 -Dundertow.host=0.0.0.0 -Dundertow.devMode=false"

# 生成 class path 值
APP_BASE_PATH=$(cd `dirname $0`; pwd)
CP=${APP_BASE_PATH}/config:${APP_BASE_PATH}/lib/*

function start()
{
    # 运行为后台进程，并在控制台输出信息
    java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} &


    # 运行为后台进程，并且不在控制台输出信息
    # nohup java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} >/dev/null 2>&1 &

    # 运行为后台进程，并且将信息输出到 output.log 文件
    #nohup java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS} > output.log &
    #tail -f /dev/null

    # 运行为非后台进程，多用于开发阶段，快捷键 ctrl + c 可停止服务
    # 当以此方式在Docker下启动时，由于是后台进程，无前台进程，Docker容器启动后会马上退出，
    # 需加命令tail -f /dev/null，就可以保持你的容器一直在前台运行
    # 或者使用以下的非后台进程运行
    #java -Djava.awt.headless=true -Xverify:none ${JAVA_OPTS} -cp ${CP} ${MAIN_CLASS}
}

function stop()
{
    kill `pgrep -f ${APP_BASE_PATH}` 2>/dev/null
}

if [[ "$COMMAND" == "start" ]]; then
	start
elif [[ "$COMMAND" == "stop" ]]; then
    stop
else
    stop
    start
fi

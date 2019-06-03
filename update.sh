#!/bin/bash
# ----------------------------------------------------------------------
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : yum install -y wget && \
# wget -O update.sh https://gitee.com/fuhai/jpress/raw/master/update.sh && \
# bash update.sh newPath oldPath
# ----------------------------------------------------------------------

newPath="$1"
oldPath="$2"

if [[ "$newPath" == "" ]]; then
	echo "./please designated new jpress path"
	exit 0
fi

if [ ! -f "$newPath/robots.txt" ];then
    echo "$newPath is not a jpress path"
fi

if [[ "$oldPath" == "" ]]; then
	echo "./please designated old jpress path"
	exit 0
fi

if [ ! -f "$oldPath/WEB-INF/classes/jboot-simple.properties" ];then
    echo "$oldPath is not a jpress path"
fi

# 备份旧的JPress
echo "backup old jpress..."
cp -rf ${oldPath} ${oldPath}_bak

# 删除对于的数据
rm -rf ${oldPath}/WEB-INF/lib
rm -rf ${oldPath}/WEB-INF/install
rm -rf ${oldPath}/WEB-INF/views
rm -rf ${oldPath}/static

# 拷贝新的jpress
echo "copy new jpress files..."
cp -rf ${newPath}/WEB-INF/lib  ${oldPath}/WEB-INF/lib
cp -rf ${newPath}/WEB-INF/install  ${oldPath}/WEB-INF/install
cp -rf ${newPath}/WEB-INF/views  ${oldPath}/WEB-INF/views
cp -rf ${newPath}/lib  ${oldPath}/static
cp -rf ${newPath}/robots.txt  ${oldPath}/robots.txt

echo "update finished, please restart tomcat..."





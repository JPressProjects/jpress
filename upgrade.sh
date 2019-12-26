#!/bin/bash
# ----------------------------------------------------------------------
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : yum install -y wget && wget https://gitee.com/fuhai/jpress/raw/master/upgrade.sh && bash upgrade.sh newPath oldPath
# ----------------------------------------------------------------------

newPath="$1"
oldPath="$2"

if [[ "$newPath" == "" ]]; then
	echo "./please designated new jpress path"
	exit 0
fi

if [ ! -f "$newPath/robots.txt" ];then
    echo "$newPath is not a jpress path"
    exit 0
fi

if [[ "$oldPath" == "" ]]; then
	echo "./please designated old jpress path"
	exit 0
fi

if [ ! -f "$oldPath/WEB-INF/classes/jboot-simple.properties" ];then
    echo "$oldPath is not a jpress path"
    exit 0
fi

bakpath=""
if [[ $oldPath == */ ]]; then
backpath=${oldPath:0-0:${#oldPath}-1}_bak
else
bakpath=${oldPath}_bak
fi


# 备份 jpress
echo "backup old jpress to $bakpath"
cp -rf ${oldPath} ${bakpath}

# 删除旧的文件
rm -rf ${oldPath}/WEB-INF/lib
rm -rf ${oldPath}/WEB-INF/install
rm -rf ${oldPath}/WEB-INF/views
rm -rf ${oldPath}/WEB-INF/web.xml
rm -rf ${oldPath}/static


echo "start upgrade ..."

# 拷贝新的jpress
cp -rf ${newPath}/WEB-INF/lib  ${oldPath}/WEB-INF/lib
cp -rf ${newPath}/WEB-INF/install  ${oldPath}/WEB-INF/install
cp -rf ${newPath}/WEB-INF/views  ${oldPath}/WEB-INF/views
cp -rf ${newPath}/WEB-INF/web.xml  ${oldPath}/WEB-INF/web.xml
cp -rf ${newPath}/static  ${oldPath}/static
cp -rf ${newPath}/robots.txt  ${oldPath}/robots.txt

echo "upgrade finished, please restart tomcat..."





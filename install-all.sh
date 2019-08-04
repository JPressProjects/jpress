#!/bin/bash
# ----------------------------------------------------------------------
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : yum install -y wget && wget -O install.sh https://gitee.com/fuhai/jpress/raw/master/install-all.sh && bash install.sh
# ----------------------------------------------------------------------


# 安装docker
# sudo yum update
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum list docker-ce --showduplicates | sort -r
sudo yum install docker-ce

# 启动docker和开机自启动
sudo systemctl start docker
sudo systemctl enable docker

# 查看版本
docker version

# 安装docker-compose
pip install --upgrade pip
pip install docker-compose
docker-compose -version

# 安装jpress
wget https://gitee.com/fuhai/jpress/raw/master/docker-compose.yml
docker-compose up -d








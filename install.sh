#!/bin/bash
# ----------------------------------------------------------------------
# author:       yangfuhai
# email:        fuhai999@gmail.com
# use : wget -O install.sh https://gitee.com/fuhai/jpress/raw/master/install.sh && bash install.sh
# ----------------------------------------------------------------------


# 安装docker
if ! [ -x "$(command -v docker)" ]; then
  echo '检测到Docker尚未安装，正在试图安装Docker...所需时间与你的网络环境有关'
  sudo -s curl -sSL https://get.daocloud.io/docker | sh

  # 启动docker和开机自启动
  sudo systemctl start docker
  sudo systemctl enable docker
fi


if ! [ -x "$(command -v docker)" ]; then
  echo '检测到Docker尚未安装，正在试图安装Docker...所需时间与你的网络环境有关'
  sudo yum install -y yum-utils device-mapper-persistent-data lvm2
  sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
  yum list docker-ce --showduplicates | sort -r
  sudo yum install docker-ce

  # 启动docker和开机自启动
  sudo systemctl start docker
  sudo systemctl enable docker
fi


 # 安装docker-compose
if ! [ -x "$(command -v docker-compose)" ]; then
  echo '检测到Docker-Compose尚未安装，正在试图安装Docker-Compose...所需时间与你的网络环境有关'
  if ! [ -x "$(command -v pip)" ]; then
      curl -L https://github.com/docker/compose/releases/download/1.25.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
      chmod +x /usr/local/bin/docker-compose
  else
      pip install --upgrade pip
      pip install docker-compose
  fi
fi


 # 开始安装 jpress
if [ -x "$(command -v docker)" -a -x "$(command -v docker-compose)" ]; then
  docker version
  docker-compose -version

  # 安装jpress
  wget https://gitee.com/fuhai/jpress/raw/master/docker-compose.yml
  docker-compose up -d

else

  if ! [ -x "$(command -v docker)" ]; then
    echo 'Docker安装失败，请检测您当前的环境（或网络）是否正常。'
  fi

  if ! [ -x "$(command -v docker-compose)" ]; then
    echo 'Docker-Compose安装失败，请检测您当前的环境（或网络）是否正常。'
  fi

fi



rm -f install.sh








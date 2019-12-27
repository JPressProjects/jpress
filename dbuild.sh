#!/bin/bash

version="$1"

if [[ "$version" == "" ]]; then
	echo "./please designated docker image version"
	exit 0
fi


mvn clean install


echo "exec : docker build . -t fuhai/jpress:"${version}
docker build . -t fuhai/jpress:${version}


echo "exec : docker push fuhai/jpress:"${version}
docker push fuhai/jpress:${version}


echo "exec : docker push fuhai/jpress:latest"
docker tag fuhai/jpress:${version} fuhai/jpress:latest
docker push fuhai/jpress:latest

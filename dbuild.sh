#!/bin/bash

version="$1"

if [[ "$version" == "" ]]; then
	echo "./please designated docker image version"
	exit 0
fi


mvn clean install


echo "exec : docker build . -t jpress:"${version}
docker build . -t fuhai/jpress:${version}


echo "exec : docker push jpress:"${version}
docker push fuhai/jpress:${version}


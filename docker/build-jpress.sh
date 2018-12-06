cp -rf ../starter-tomcat/target/starter-tomcat-1.0.war .
docker build . -t jpress:latest -f ./Dockerfile.jpress
rm -rf starter-tomcat-1.0.war
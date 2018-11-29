cp -rf ../starter-tomcat/target/starter-tomcat-1.0.war .
docker build . -t jpress:latest
rm -rf starter-tomcat-1.0.war
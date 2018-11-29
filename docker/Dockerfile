from tomcat:8.5.35

MAINTAINER fuhai fuhai999@gmail.com

RUN rm /usr/local/tomcat/webapps -rf

COPY starter-tomcat-1.0.war /usr/local/tomcat/webapps/ROOT.war

RUN unzip -oq /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT

RUN rm /usr/local/tomcat/webapps/ROOT.war
RUN rm /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/install.lock
RUN rm /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/jboot.properties

FROM centos:7.6.1810

USER root

#安装必须软件
RUN	yum install -y wget && \
yum install -y tar && \
yum install -y git && \
yum install -y unzip


#JAVA 版本
ENV JAVA_VERSION 1.8.0

#安装Java
RUN yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

#设置 Java Home
ENV JAVA_HOME /usr/lib/jvm/java


#安装Tomcat
ENV TOMCAT_MAJOR_VERSION 8
ENV TOMCAT_MINOR_VERSION 8.5.37


RUN wget -q http://mirror.bit.edu.cn/apache/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz && \
wget -qO- https://www.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz.sha512 | sha512sum -c - && \
tar zxf apache-tomcat-*.tar.gz && \
mkdir /usr/local/tomcat && \
mv apache-tomcat-${TOMCAT_MINOR_VERSION}/* /usr/local/tomcat && \
rm -rf /usr/local/tomcat/webapps/* && \
rm -rf apache-tomcat-*


#安装Maven
ENV MAVEN_VERSION_MAJOR 3
ENV MAVEN_VERSION_MINOR 6.0

RUN wget -q http://mirror.bit.edu.cn/apache/maven/maven-$MAVEN_VERSION_MAJOR/$MAVEN_VERSION_MAJOR.$MAVEN_VERSION_MINOR/binaries/apache-maven-${MAVEN_VERSION_MAJOR}.${MAVEN_VERSION_MINOR}-bin.tar.gz  && \
tar xvf apache-maven-${MAVEN_VERSION_MAJOR}.${MAVEN_VERSION_MINOR}-bin.tar.gz && \
rm apache-maven-${MAVEN_VERSION_MAJOR}.${MAVEN_VERSION_MINOR}-bin.tar.gz && \
mkdir /usr/local/maven && \
mv apache-maven-${MAVEN_VERSION_MAJOR}.${MAVEN_VERSION_MINOR}/* /usr/local/maven

ENV M2_HOME=/usr/local/maven
ENV M2=$M2_HOME/bin

ENV PATH=$M2:$PATH


#安装编译jpress
RUN git clone https://github.com/JpressProjects/jpress.git && \
cd jpress && \
mvn clean install && \
cp -rf ./starter-tomcat/target/starter-tomcat-1.0.war /usr/local/tomcat/webapps/ROOT.war && \
unzip -oq /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT


RUN cd .. && \
rm -rf jpress && \
rm -rf /usr/local/tomcat/webapps/ROOT.war && \
rm -rf /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/install.lock  && \
rm -rf /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/jboot.properties && \
rm -rf /usr/local/maven


EXPOSE 8080

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
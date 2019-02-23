FROM maven:3.6-jdk-8-alpine
LABEL maintainer="Michael Yang<fuhai999@gmail.com>"

WORKDIR /opt/jpress

ADD . /tmp

ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone

RUN cd /tmp && \
    cp -f /tmp/docker/build/settings.xml /usr/share/maven/conf/settings.xml && \
    mvn package -Pci && \
    mv starter/target/starter-2.0/* /opt/jpress/ && \
    cp -f /tmp/docker/build/jpress.sh /opt/jpress/jpress.sh &&  \
    cp -f /tmp/docker/build/jboot.properties /opt/jpress/config/jboot.properties && \
    rm -rf /tmp && \
    rm -rf ~/.m2 && \
    rm -rf /opt/jpress/jpress.bat && \
    rm -rf /opt/jpress/config/undertow.txt && \
    rm -rf /opt/jpress/config/install.lock

EXPOSE 8080

CMD ["/opt/jpress/jpress.sh", "start"]
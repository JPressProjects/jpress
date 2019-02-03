FROM maven:3-jdk-8-alpine
LABEL maintainer="Michael Yang<fuhai999@gmail.com>"

WORKDIR /opt/jpress

ADD . /tmp

ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone

RUN cd /tmp && \
    mvn package -Pci &&  \
    mv starter/target/starter-2.0-release/starter-2.0/* /opt/jpress/ && \
    rm -rf /usr/share/maven && \
    rm -rf /tmp && \
    rm -rf ~/.m2 && \
    rm -rf /opt/jpress/config/jboot.properties && \
    rm -rf /opt/jpress/config/install.lock

EXPOSE 8080

CMD ["/opt/jpress/jpress.sh", "start"]
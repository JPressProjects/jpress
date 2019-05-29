FROM openjdk:8-jdk-alpine
LABEL maintainer="Michael Yang<fuhai999@gmail.com>"

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone

COPY ./files/wqy-zenhei.ttc  /usr/lib/jvm/java-1.8-openjdk/jre/lib/fonts/wqy-zenhei.ttc

RUN apk add --no-cache curl tar bash procps ttf-dejavu
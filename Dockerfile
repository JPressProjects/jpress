FROM fuhai/jpress:v5.0.3
LABEL maintainer="Michael Yang<fuhai999@gmail.com>"

WORKDIR /opt/jpress

EXPOSE 8080

CMD ["/opt/jpress/jpress.sh", "start"]
#FROM openjdk:11
FROM openjdk:11-jre-slim-buster

ARG PORT=80
ARG WDIR=/usr/resthome
ARG RESOURCE=/var/resources

EXPOSE ${PORT}/tcp

RUN  mkdir ${RESOURCE}; \ 
mkdir ${WDIR}; \
apt-get -y update; \
apt-get -y install python3-pip; \
pip3 install JayDeBeApi; \
echo "PORT=${PORT}" > $WDIR/env.sh; \
echo "RESOURCE=${RESOURCE}" >> $WDIR/env.sh; \
echo "WDIR=${WDIR}" >> $WDIR/env.sh


VOLUME ["/var/resources"]

COPY *jar ${WDIR}
COPY *sh ${WDIR}
WORKDIR ${WDIR}


CMD ["bash","-c","./run.sh"]
#CMD ["bash","-c","sleep infinity"]
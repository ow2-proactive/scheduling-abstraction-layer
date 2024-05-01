FROM tomcat:9.0.68-jdk8-corretto-al2

RUN rm -rf ${CATALINA_HOME}/webapps/*
# NOTE: Switch between local or public builded SAL
#RUN curl -o /usr/local/tomcat/webapps/sal.war http://repository.activeeon.com/content/groups/proactive/org/ow2/proactive/scheduling-abstraction-layer/13.1.0-SNAPSHOT/scheduling-abstraction-layer-13.1.0-20221027.125525-9.war
COPY sal-service/build/libs/scheduling-abstraction-layer-*.war /usr/local/tomcat/webapps/sal.war

ENV EXTERNAL_CONFIG_DIR=${CATALINA_HOME}/conf
ENV PROPERTIES_FILENAME='sal'

ENV PA_HOME=${CATALINA_HOME}

COPY  docker/scheduling-abstraction-layer.xml ${EXTERNAL_CONFIG_DIR}/Catalina/localhost/sal.xml
# NOTE: Please make sure if you set a different `PROPERTIES_FILENAME` to copy your properties file inside the container
COPY  docker/sal.application.properties ${EXTERNAL_CONFIG_DIR}/${PROPERTIES_FILENAME}.properties
COPY docker/wait_for_db.sh /usr/local/tomcat/bin

ENV PWS_URL='http://localhost:8080/'
ENV PWS_USERNAME='admin'
ENV PWS_PASSWORD='admin'

ENV DB_USERNAME='root'
ENV DB_PASSWORD=''

ENV DB_DRIVER_CLASSNAME='org.mariadb.jdbc.Driver'
ENV DB_URL='jdbc:mariadb://localhost:3306/proactive'
ENV DB_PLATFORM='org.hibernate.dialect.MariaDB53Dialect'

RUN yum install -y nmap-ncat
RUN chmod +x /usr/local/tomcat/bin/wait_for_db.sh
CMD ["/bin/bash", "-c", "wait_for_db.sh && catalina.sh jpda run"]


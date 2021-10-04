FROM ubuntu:18.04

RUN apt-get clean all
RUN apt-get update

# Open-JDK
RUN apt update
RUN apt -y install wget curl
RUN curl -O https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
RUN tar xvf openjdk-14.0.1_linux-x64_bin.tar.gz
RUN mv jdk-14.0.1 /opt/
ENV JAVA_HOME=/opt/jdk-14.0.1
ENV PATH=$PATH:$JAVA_HOME/bin
## Maven
RUN curl -O --insecure https://mirrors.ocf.berkeley.edu/apache/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz
RUN tar xzvf apache-maven-3.8.3-bin.tar.gz
RUN mv apache-maven-3.8.3 /opt/
ENV PATH=$PATH:/opt/apache-maven-3.8.1/bin

# Tomcat
RUN useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.34/bin/apache-tomcat-9.0.34.tar.gz -P /tmp
RUN tar xf /tmp/apache-tomcat-9*.tar.gz -C /opt/tomcat
RUN ln -s /opt/tomcat/apache-tomcat-9.0.34 /opt/tomcat/latest
RUN chown -RH tomcat: /opt/tomcat/latest
RUN sh -c 'chmod +x /opt/tomcat/latest/bin/*.sh'
ENV CATALINA_OUT=/dev/null

RUN mkdir /mnt/javatron
ADD ./javatron /mnt/javatron
WORKDIR /mnt/javatron

RUN mvn clean package
RUN cp target/*.war /opt/tomcat/latest/webapps/api.war

RUN sed -i "s,8080,8081," /opt/tomcat/latest/conf/server.xml

EXPOSE 8081

CMD [ "/opt/tomcat/latest/bin/catalina.sh", "run"]

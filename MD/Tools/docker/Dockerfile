FROM centos:7
MAINTAINER mhc
EXPOSE 8080
RUN cd /usr/local/bin
RUN yum install java-1.8.0-openjdk.x86_64 -y
RUN yum install wget.x86_64 -y
RUN wget https://apache.website-solution.net/tomcat/tomcat-8/v8.5.69/bin/apache-tomcat-8.5.69.tar.gz
RUN tar -xzvf apache-tomcat-8.5.50.tar.gz -C /usr/local/bin
VOLUME /usr/local/bin/apache-tomcat-8.5.50/logs
VOLUME /usr/local/bin/apache-tomcat-8.5.50/conf
CMD /usr/local/bin/apache-tomcat-8.5.50/bin/startup.sh && tail -f /usr/local/bin/apache-tomcat-8.5.50/bin/startup.sh


FROM tomcat

COPY . .

RUN apt-get update ; apt-get install maven default-jdk -y ; update-alternatives --config javac

RUN mvn clean package ; cp target/*.war /usr/local/tomcat/webapps/
ADD catalina.policy /usr/local/tomcat/conf
ADD src/main/webapp/META-INF/context.xml /usr/local/tomcat/conf/Catalina/localhost/JavaVulnerableLab.xml
CMD ["catalina.sh","run","-security"]

FROM openjdk:8
MAINTAINER Lisa-Marie Hege <lisamariehege@gmail.com>
COPY . /src/java
WORKDIR /src/java
RUN javac HttpServer.java
CMD ["java", "HttpServer"]

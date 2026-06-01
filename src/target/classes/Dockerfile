FROM ubuntu:22.04

ARG DISPLAY
ARG DEBIAN_FRONTEND
ARG TZ
ARG LANG
ARG LC_ALL
ARG MAVEN_VERSION
ARG JAVA_VERSION

ENV DISPLAY=${DISPLAY}
ENV DEBIAN_FRONTEND=${DEBIAN_FRONTEND}
ENV TZ=${TZ}
ENV LANG=${LANG}
ENV LC_ALL=${LC_ALL}
ENV MAVEN_VERSION=${MAVEN_VERSION}
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-arm64
ENV PATH=/opt/maven/bin:${PATH}

WORKDIR /app

# Install Java
RUN apt-get update \
    && apt-get install -y \
        openjdk-${JAVA_VERSION}-jdk \
        xvfb x11vnc fluxbox novnc python3-websockify \
        libxext6 libxrender1 libxtst6 libxi6 fontconfig \
        tar ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Install Maven
RUN wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

COPY ./pom.xml ./pom.xml
COPY . .

RUN mvn clean
RUN mvn compile
RUN mvn test 
RUN mvn package 
RUN mvn install 
RUN mvn dependency:copy-dependencies 

CMD ["/bin/sh", "-c", "\ 
    Xvfb :0 -screen 0 1024x768x24 -ac & \ 
    sleep 2 && \ 
    fluxbox & \ 
    sleep 1 && \ 
    x11vnc -forever -shared -display :0 -nopw -listen 0.0.0.0 -bg && \ 
    sleep 1 && \ 
    websockify --web=/usr/share/novnc 6901 localhost:5900 && \ 
    java -jar target/project-1.0-SNAPSHOT.jar  \
    "]

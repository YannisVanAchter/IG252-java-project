FROM ubuntu:22.04
WORKDIR /app

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Paris
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

RUN apt-get update \
    && apt-get install -y openjdk-17-jdk xvfb x11vnc fluxbox novnc python3-websockify \
        libxext6 libxrender1 libxtst6 libxi6 fontconfig \
    && rm -rf /var/lib/apt/lists/*

CMD ["sh", "-c", "javac -cp 'lib/*' Main.java && Xvfb :0 -screen 0 1024x768x24 & fluxbox & x11vnc -forever -shared -display :0 -nopw -listen 0.0.0.0 -bg && websockify --web=/usr/share/novnc 6901 localhost:5900 & java -cp '.:/app/lib/*' Main"]

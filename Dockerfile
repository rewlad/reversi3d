FROM eclipse-temurin:11-jdk AS build
WORKDIR /workspace

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

RUN curl -fLo /usr/local/bin/cs https://github.com/coursier/coursier/releases/download/v2.1.25-M19/coursier && \
    chmod +x /usr/local/bin/cs

COPY src ./src
COPY war ./war

RUN set -eux; \
    mkdir -p build/WEB-INF/classes; \
    deps="$(cs fetch --classpath javax.servlet:javax.servlet-api:3.1.0)"; \
    javac --release 8 -encoding UTF-8 -cp "$deps" -d build/WEB-INF/classes $(find src -name '*.java'); \
    cp -r war/. build/; \
    (cd build && jar -cf ../reversi3d.war .)

FROM jetty:9.4-jre8
COPY --from=build /workspace/reversi3d.war /var/lib/jetty/webapps/root.war

EXPOSE 8080

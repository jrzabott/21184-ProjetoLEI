# ── Stage 1: build ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# cache de dependencias separado do source (rebuild mais rapido)
COPY pom.xml .
RUN mvn -ntp dependency:go-offline -q

COPY src ./src
# testes E2E precisam de Chrome — sao excluidos aqui; os unitarios correm em CI
RUN mvn -ntp package -DskipTests -q

# ── Stage 2: runtime ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
COPY --from=builder /build/target/musical-trainer-1.0.0-SNAPSHOT.jar app.jar
COPY frontend ./frontend

# /data para a BD SQLite (deve ser montado como volume em producao)
RUN mkdir -p /data && chown -R app:app /app /data

USER app
EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xmx192m", \
  "-Xss512k", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar", \
  "--spring.profiles.active=prod"]

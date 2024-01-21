FROM gradle:8.5.0-jdk17-alpine as build_providers
# TODO: make that all builds with nix

WORKDIR /out

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle clean build --no-daemon

FROM quay.io/keycloak/keycloak:23.0.3 as builder
# from: https://www.keycloak.org/server/containers#_creating_a_customized_and_optimized_container_image

WORKDIR /opt/keycloak

COPY --from=build_providers /out/build/libs/ /opt/keycloak/providers

RUN keytool -genkeypair \
    -storepass password  \
    -storetype PKCS12  \
    -keyalg RSA  \
    -keysize 2048  \
    -dname "CN=server"  \
    -alias server  \
    -ext "SAN:c=DNS:localhost,IP:127.0.0.1"  \
    -keystore conf/server.keystore

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:23.0.3

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
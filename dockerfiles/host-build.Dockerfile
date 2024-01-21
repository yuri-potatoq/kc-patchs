FROM quay.io/keycloak/keycloak:23.0.3 as providers_builder
# from: https://www.keycloak.org/server/containers#_creating_a_customized_and_optimized_container_image

WORKDIR /opt/keycloak

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

FROM quay.io/keycloak/keycloak:23.0.3 as final

COPY --from=providers_builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

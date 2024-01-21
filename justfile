
env-file := env_var_or_default("ENV_FILE", ".env")
compose-up := "podman-compose --env-file " + env-file + " up -d --build"


# can target container|host build
run-kc target="container":
    {{ compose-up }} kc-{{ target }}-build

run-kafka:
    {{ compose-up }} kafka


kcat-exec := "podman run -it --network=kc_subnet edenhill/kcat:1.7.1"

read-topic topic_name:
    {{ kcat-exec }} -C -b host.docker.internal:9092 \
        -f 'Topic %t[%p], offset: %o, key: %k, payload: %s\n' -o beginning \
        -t "{{ topic_name }}" -p @
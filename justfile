
env-file := env_var_or_default("ENV_FILE", ".env")
compose-up := "podman-compose --env-file " / env-file / " up -d --build"

run-kc-local:
    {{ compose-up }} kc-local

run-kafka-local:
    {{ compose-up }} kafka


read-topic topic_name:
    kcat -C -b 0.0.0.0:9092 \
        -f 'Topic %t[%p], offset: %o, key: %k, payload: %s\n' -o beginning \
        -t "{{ topic_name }}" -p @
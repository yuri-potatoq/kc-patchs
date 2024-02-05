package com.github.yuri_potatoq

import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*


typealias DefaultPropsHelper = Properties.( (String, Any) -> Any ) -> Unit


sealed class KafkaContext(val props: Properties) {

    class ProtobufProducerContext : KafkaContext(
        Properties().applyDefaultProps {
            this@applyDefaultProps[KafkaProtobufSerializerConfig.AUTO_REGISTER_SCHEMAS] = true
            this@applyDefaultProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer"
            this@applyDefaultProps[KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = it("SCHEMA_REGISTRY_URL", "http://0.0.0.0:8081")
        }
    )

    class AvroProducerContext : KafkaContext(
        Properties().applyDefaultProps { }
    )

    class JsonProducerContext : KafkaContext(
        Properties().applyDefaultProps { }
    )

    companion object {
        private const val ENV_VAR_PREFIX = "KFK_EVL_"

        private val env = System.getenv()

        private fun getEnvOrDefault(name: String, dft: Any) = env.getOrDefault(ENV_VAR_PREFIX+name, dft)

        private inline fun Properties.applyDefaultProps(block: DefaultPropsHelper): Properties {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = getEnvOrDefault("BOOTSTRAP_SERVERS", "0.0.0.0:9092")
            this[ProducerConfig.ACKS_CONFIG] = "all"
            this[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
            this[ProducerConfig.BUFFER_MEMORY_CONFIG] = 33554432
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
            block(::getEnvOrDefault)
            return this
        }
    }
}
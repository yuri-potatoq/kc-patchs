package com.github.yuri_potatoq.brokers

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties

class KafkaContext {

    companion object {
        fun <R>getProducer() : KafkaProducer<String, R> {

            val props = Properties()
            props["bootstrap.servers"] = "host.docker.internal:9092"
            props.put("acks", "all")
            props.put("batch.size", 16384)
            props.put("buffer.memory", 33554432)

            // props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
            props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer"
            props["schema.registry.url"] = "http://127.0.0.1:8081"
            props["auto.register.schemas"] = false

            return KafkaProducer<String, R>(props)
        }
    }
}
package com.github.yuri_potatoq.brokers

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.Properties

class KafkaContext {

    companion object {
        fun getProducer() : KafkaProducer<String, String> {
            val props = Properties()
            props["bootstrap.servers"] = "host.docker.internal:9092"
            props.put("acks", "all")
            props.put("batch.size", 16384)
            props.put("buffer.memory", 33554432)
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
            props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

            return KafkaProducer<String, String>(props)
        }
    }
}
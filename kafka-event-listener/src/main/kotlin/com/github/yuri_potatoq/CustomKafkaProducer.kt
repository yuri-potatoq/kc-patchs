package com.github.yuri_potatoq

import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.jboss.logging.Logger
import java.util.concurrent.Future

sealed class CustomKafkaProducer<T>(private var topic: String) {
    private constructor(topic: String, ctx: KafkaContext) : this(topic) {
        this.producer = KafkaProducer(ctx.props)
    }

    private lateinit var producer: KafkaProducer<String, T>

    private val callback = defaultKafkaProducerCallback()

    companion object {
        private val logger = Logger.getLogger(CustomKafkaProducer::class.java)
    }

    private fun defaultKafkaProducerCallback() = Callback { metadata: RecordMetadata?, exception: Exception? ->
        if (exception == null)
            logger.infof("Message sent with topic: %s, partition: %s, offset: %s",
                metadata?.topic(), metadata?.partition(), metadata?.offset())
        else
            logger.infof("Message not sent, Error: %s", exception.toString())
    }

     fun send(key: String, message: T): Future<RecordMetadata> =
        producer.send(ProducerRecord(topic, key, message), callback)

    class ProtobufKafkaProducer<T>(topic: String) :
        CustomKafkaProducer<T>(topic, KafkaContext.ProtobufProducerContext())
}
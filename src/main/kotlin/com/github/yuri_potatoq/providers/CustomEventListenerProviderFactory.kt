package com.github.yuri_potatoq.providers

import com.github.yuri_potatoq.brokers.KafkaContext
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.jboss.logging.Logger
import org.keycloak.Config
import org.keycloak.events.Event
import org.keycloak.events.EventListenerProvider
import org.keycloak.events.EventListenerProviderFactory
import org.keycloak.events.admin.AdminEvent
import org.keycloak.models.KeycloakSession
import org.keycloak.models.KeycloakSessionFactory


class CustomEventListenerProviderFactory : EventListenerProviderFactory {
    companion object {
        private val logger = Logger.getLogger(
            CustomEventListenerProviderFactory::class.java
        )

        val kafkaProducer = KafkaContext.getProducer()

        val customEventListener = object : EventListenerProvider {
            override fun onEvent(event: Event?) {
                logger.infof("Event name: %s Realm: %s", event?.id, event?.realmId)

                val callback = Callback { metadata: RecordMetadata?, exception: Exception? ->
                    if (exception == null)
                        logger.infof("Message sent topic: %s, partition: %s, offset: %s",
                            metadata?.topic(), metadata?.partition(), metadata?.offset())
                    else logger.infof("Message sent Error: %s", exception.toString())
                }

                var rec = kafkaProducer.send(
                    ProducerRecord<String, String>("topic_test", "test", "test")
                    , callback)


                logger.infof("Sended To KAfka")
            }

            override fun onEvent(event: AdminEvent?, includeRepresentation: Boolean) { }

            override fun close() { }
        }
    }

    override fun create(keycloakSession: KeycloakSession?) = customEventListener

    override fun init(scope: Config.Scope?) { }
    override fun postInit(keycloakSessionFactory: KeycloakSessionFactory?) {}
    override fun close() {}
    override fun getId() = "kafka-event-listener"
}
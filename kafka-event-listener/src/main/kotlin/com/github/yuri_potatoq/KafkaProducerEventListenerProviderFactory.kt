package com.github.yuri_potatoq

import com.github.yuri_potatoq.CustomKafkaProducer
import keycloak_events.UserEventOuterClass
import keycloak_events.UserEventOuterClass.UserEvent
import keycloak_events.UserEventOuterClass.ClientInfo
import keycloak_events.UserEventOuterClass.RealmInfo
import keycloak_events.UserEventOuterClass.UserInfo
import org.jboss.logging.Logger
import org.keycloak.Config
import org.keycloak.events.Event
import org.keycloak.events.EventListenerProvider
import org.keycloak.events.EventListenerProviderFactory
import org.keycloak.events.admin.AdminEvent
import org.keycloak.models.GroupModel
import org.keycloak.models.KeycloakSession
import org.keycloak.models.KeycloakSessionFactory
import java.util.stream.Collectors


class KafkaProducerEventListenerProviderFactory : EventListenerProviderFactory {
    companion object {
        private val logger = Logger.getLogger(
            KafkaProducerEventListenerProviderFactory::class.java
        )

        private val TOPIC_NAME = System.getenv("KFK_EVL_TOPIC_NAME")

        val protobufProducer = CustomKafkaProducer.ProtobufKafkaProducer<UserEvent>(TOPIC_NAME)
    }

    override fun create(keycloakSession: KeycloakSession?) = object : EventListenerProvider {
        override fun onEvent(event: Event?) {
            val realm = keycloakSession?.realms()?.getRealm(event?.realmId)
            val realmClient = keycloakSession?.clients()?.getClientById(realm, event?.clientId)
            val user = keycloakSession?.users()?.getUserById(realm, event?.userId)

            logger.infof("Event Name: %s Realm: %s UserId: %s", event?.id, event?.realmId, user?.id)

            val eventData:  UserEvent = UserEvent.newBuilder()
                .setClient(
                    ClientInfo.newBuilder()
                        .setId(realmClient?.id ?: "")
                        .setName(realmClient?.name ?: ""))
                .setRealm(
                    RealmInfo.newBuilder()
                        .setId(realm?.id ?: "")
                        .setName(realm?.name ?: ""))
                .setUser(
                    UserInfo.newBuilder()
                        .setId(user?.id ?: "")
                        .setEmail(user?.email ?: "")
                        .setIdpName("")
                        .setFirstName(user?.firstName ?: "")
                        .setLastName(user?.lastName ?: "")
                        .addAllGroups(
                            user?.groupsStream
                                ?.map(GroupModel::getName)
                                ?.filter { s -> !s.isNullOrEmpty() }
                                ?.collect(Collectors.toList()) ?: listOf()))
                .setError(event?.error ?: "")
                .setType(
                    UserEventOuterClass.EventType
                        .valueOf(event?.type?.toString() ?: ""))
                .putAllDetails(event?.details ?: mapOf())
                .build()

            protobufProducer.send(event?.sessionId ?: "", eventData)
        }

        override fun onEvent(event: AdminEvent?, includeRepresentation: Boolean) { }

        override fun close() { }
    }

    override fun init(scope: Config.Scope?) {}
    override fun postInit(keycloakSessionFactory: KeycloakSessionFactory?) {}
    override fun close() {}
    override fun getId() = "kafka-event-listener"
}
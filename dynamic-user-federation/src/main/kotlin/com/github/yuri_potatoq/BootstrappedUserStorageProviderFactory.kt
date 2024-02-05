package com.github.yuri_potatoq

import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.storage.UserStorageProviderFactory

object BootstrappedUserStorageProviderFactory : UserStorageProviderFactory<BootstrappedUserStorageProvider> {
    private val instance by lazy { BootstrappedUserStorageProvider() }

    override fun create(session: KeycloakSession?, model: ComponentModel?) = instance

    override fun getId(): String {
        TODO("Not yet implemented")
    }
}
package com.github.yuri_potatoq


import org.keycloak.credential.CredentialInput
import org.keycloak.credential.CredentialInputUpdater
import org.keycloak.credential.CredentialInputValidator
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.cache.OnUserCache
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProviderFactory
import org.keycloak.storage.user.UserLookupProvider
import org.keycloak.storage.user.UserQueryProvider
import org.keycloak.storage.user.UserRegistrationProvider


import java.util.stream.Stream

class BootstrappedUserStorageProvider :
    UserStorageProvider,
    UserLookupProvider,
    CredentialInputValidator,
    CredentialInputUpdater
    // UserRegistrationProvider,
    // UserQueryProvider,
    // OnUserCache
{

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun getUserById(realm: RealmModel?, id: String?): UserModel {
        TODO("Not yet implemented")
    }

    override fun getUserByUsername(realm: RealmModel?, username: String?): UserModel {
        TODO("Not yet implemented")
    }

    override fun getUserByEmail(realm: RealmModel?, email: String?): UserModel {
        TODO("Not yet implemented")
    }

    override fun updateCredential(realm: RealmModel?, user: UserModel?, input: CredentialInput?): Boolean {
        TODO("Not yet implemented")
    }

    override fun disableCredentialType(realm: RealmModel?, user: UserModel?, credentialType: String?) {
        TODO("Not yet implemented")
    }

    override fun getDisableableCredentialTypesStream(realm: RealmModel?, user: UserModel?): Stream<String> {
        TODO("Not yet implemented")
    }

    override fun isConfiguredFor(realm: RealmModel?, user: UserModel?, credentialType: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isValid(realm: RealmModel?, user: UserModel?, credentialInput: CredentialInput?): Boolean {
        TODO("Not yet implemented")
    }

    override fun supportsCredentialType(credentialType: String?): Boolean {
        TODO("Not yet implemented")
    }

}
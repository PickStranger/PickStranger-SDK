package com.example.pickstranger.auth

sealed interface PickStrangerCredential {
    data class Jwt(val token: String) : PickStrangerCredential
    data class ApiKey(val key: String) : PickStrangerCredential
    data class Mtls(
        val certPath: String,
        val keyPath: String,
        val caCertPath: String? = null
    ) : PickStrangerCredential
    data object None : PickStrangerCredential
}

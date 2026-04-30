package com.example.pickstranger.client

import com.example.pickstranger.EnvironmentUpdateResponse
import com.example.pickstranger.RevEnvironment
import com.google.common.util.concurrent.ListenableFuture

interface PickStrangerEnvironmentClient {
    fun getEnvSync(serviceName: String): RevEnvironment

    fun updateEnvSync(env: RevEnvironment): EnvironmentUpdateResponse

    fun getEnvAsync(serviceName: String): ListenableFuture<RevEnvironment>

    fun updateEnvAsync(env: RevEnvironment): ListenableFuture<EnvironmentUpdateResponse>

    @JvmSynthetic
    suspend fun getEnvCoroutine(serviceName: String): RevEnvironment

    @JvmSynthetic
    suspend fun updateEnvCoroutine(env: RevEnvironment): EnvironmentUpdateResponse

    fun shutdown()
}
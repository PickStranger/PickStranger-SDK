package com.example.pickstranger.client.impl

import com.example.pickstranger.EnvironmentRequester
import com.example.pickstranger.EnvironmentUpdateResponse
import com.example.pickstranger.RevEnvironment
import com.example.pickstranger.RevEnvironmentConfigServiceGrpc
import com.example.pickstranger.RevEnvironmentConfigServiceGrpcKt
import com.example.pickstranger.client.EnvironmentClient
import com.google.common.util.concurrent.ListenableFuture
import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit

internal class EnvironmentClientImpl(private val channel: ManagedChannel) : EnvironmentClient {
    private val blockingStub = RevEnvironmentConfigServiceGrpc.newBlockingStub(channel)
    private val futureStub = RevEnvironmentConfigServiceGrpc.newFutureStub(channel)
    private val coroutineStub = RevEnvironmentConfigServiceGrpcKt.RevEnvironmentConfigServiceCoroutineStub(channel)

    override fun getEnvSync(serviceName: String): RevEnvironment {
        val request = EnvironmentRequester.newBuilder().setServiceName(serviceName).build()
        return blockingStub.getEnvironment(request)
    }

    override fun updateEnvSync(env: RevEnvironment): EnvironmentUpdateResponse {
        return blockingStub.updateEnvironment(env)
    }

    override fun getEnvAsync(serviceName: String): ListenableFuture<RevEnvironment> {
        val request = EnvironmentRequester.newBuilder().setServiceName(serviceName).build()
        return futureStub.getEnvironment(request)
    }

    override fun updateEnvAsync(env: RevEnvironment): ListenableFuture<EnvironmentUpdateResponse> {
        return futureStub.updateEnvironment(env)
    }

    override suspend fun getEnvCoroutine(serviceName: String): RevEnvironment {
        val request = EnvironmentRequester.newBuilder().setServiceName(serviceName).build()
        return coroutineStub.getEnvironment(request)
    }

    override suspend fun updateEnvCoroutine(env: RevEnvironment): EnvironmentUpdateResponse {
        return coroutineStub.updateEnvironment(env)
    }

    override fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
package com.example.pickstranger.configuration

import com.example.pickstranger.auth.PickStrangerCredential
import com.example.pickstranger.auth.PickStrangerInterceptor
import com.example.pickstranger.auth.PickStrangerServerInterceptor
import com.example.pickstranger.client.EnvironmentClient
import com.example.pickstranger.client.impl.EnvironmentClientImpl
import com.example.pickstranger.configuration.PickStrangerProperties.AuthType
import com.example.pickstranger.handler.RiskScoreHandler
import com.example.pickstranger.handler.RiskScoreListenerServiceImpl
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.SmartLifecycle
import org.springframework.context.annotation.Bean
import java.io.File

@AutoConfiguration
@EnableConfigurationProperties(PickStrangerProperties::class)
internal class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun pickStrangerEnvironmentClient(properties: PickStrangerProperties): EnvironmentClient {
        val credential = buildCredential(properties.auth)
        val channel = buildChannel(properties.grpc, credential)
        return EnvironmentClientImpl(channel)
    }

    // RiskScoreHandler 빈이 있을 때만 gRPC 서버 기동 (reversed proxy가 연결해 올 서버)
    @Bean
    @ConditionalOnBean(RiskScoreHandler::class)
    fun riskScoreGrpcServerLifecycle(
        handler: RiskScoreHandler,
        properties: PickStrangerProperties
    ): SmartLifecycle {
        val serverBuilder = ServerBuilder.forPort(properties.server.port)
            .addService(RiskScoreListenerServiceImpl(handler))

        if (properties.security.secret.isNotBlank()) {
            serverBuilder.intercept(
                PickStrangerServerInterceptor(
                    secret = properties.security.secret,
                    timestampToleranceSeconds = properties.security.timestampTolerance
                )
            )
        }

        val server = serverBuilder.build()

        return object : SmartLifecycle {
            private var running = false

            override fun start() {
                server.start()
                running = true
            }

            override fun stop() {
                server.shutdown()
                running = false
            }

            override fun isRunning() = running

            // Spring 컨텍스트에서 가장 마지막에 시작, 가장 먼저 종료
            override fun getPhase() = Int.MAX_VALUE
        }
    }

    private fun buildCredential(auth: PickStrangerProperties.AuthProperties): PickStrangerCredential =
        when (auth.type) {
            AuthType.JWT -> PickStrangerCredential.Jwt(auth.jwt.token)
            AuthType.API_KEY -> PickStrangerCredential.ApiKey(auth.apiKey.value)
            AuthType.MTLS -> PickStrangerCredential.Mtls(
                certPath = auth.mtls.certPath,
                keyPath = auth.mtls.keyPath,
                caCertPath = auth.mtls.caCertPath.takeIf { it.isNotBlank() }
            )
            AuthType.NONE -> PickStrangerCredential.None
        }

    private fun buildChannel(
        grpc: PickStrangerProperties.GrpcProperties,
        credential: PickStrangerCredential
    ): ManagedChannel = when (credential) {
        is PickStrangerCredential.Mtls -> buildMtlsChannel(grpc, credential)
        else -> buildPlainChannel(grpc, credential)
    }

    private fun buildPlainChannel(
        grpc: PickStrangerProperties.GrpcProperties,
        credential: PickStrangerCredential
    ): ManagedChannel {
        val builder = ManagedChannelBuilder.forAddress(grpc.host, grpc.port).usePlaintext()
        if (credential !is PickStrangerCredential.None) {
            builder.intercept(PickStrangerInterceptor(credential))
        }
        return builder.build()
    }

    private fun buildMtlsChannel(
        grpc: PickStrangerProperties.GrpcProperties,
        credential: PickStrangerCredential.Mtls
    ): ManagedChannel {
        val sslContextBuilder = GrpcSslContexts.forClient()
            .keyManager(File(credential.certPath), File(credential.keyPath))
        credential.caCertPath?.let { sslContextBuilder.trustManager(File(it)) }

        return NettyChannelBuilder.forAddress(grpc.host, grpc.port)
            .sslContext(GrpcSslContexts.configure(sslContextBuilder).build())
            .build()
    }
}

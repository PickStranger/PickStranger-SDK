package com.example.pickstranger.configuration
import com.example.pickstranger.client.EnvironmentClient
import com.example.pickstranger.client.impl.EnvironmentClientImpl
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
internal class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun pickStrangerEnvironmentClient(): EnvironmentClient {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        return EnvironmentClientImpl(channel)
    }
}
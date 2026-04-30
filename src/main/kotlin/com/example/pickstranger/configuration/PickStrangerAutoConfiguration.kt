package com.example.pickstranger.configuration
import com.example.pickstranger.client.PickStrangerEnvironmentClient
import com.example.pickstranger.client.impl.PickStrangerEnvironmentClientImpl
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
class PickStrangerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun pickStrangerEnvironmentClient(): PickStrangerEnvironmentClient {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        return PickStrangerEnvironmentClientImpl(channel)
    }
}
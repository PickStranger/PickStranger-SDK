package com.example.pickstranger.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pickstranger")
data class PickStrangerProperties(
    val grpc: GrpcProperties = GrpcProperties(),
    val server: ServerProperties = ServerProperties(),
    val auth: AuthProperties = AuthProperties(),
    val security: SecurityProperties = SecurityProperties()
) {
    data class GrpcProperties(
        val host: String = "localhost",
        val port: Int = 9090
    )

    data class ServerProperties(
        val port: Int = 9091
    )

    data class AuthProperties(
        val type: AuthType = AuthType.NONE,
        val jwt: JwtProperties = JwtProperties(),
        val apiKey: ApiKeyProperties = ApiKeyProperties(),
        val mtls: MtlsProperties = MtlsProperties()
    )

    enum class AuthType { NONE, JWT, API_KEY, MTLS }

    data class JwtProperties(val token: String = "")
    data class ApiKeyProperties(val value: String = "")
    data class MtlsProperties(
        val certPath: String = "",
        val keyPath: String = "",
        val caCertPath: String = ""
    )

    // 프록시 → 서비스 앱 gRPC 서버 수신 검증용
    data class SecurityProperties(
        val secret: String = "",              // 프록시와 공유하는 시크릿 키
        val timestampTolerance: Long = 300    // 허용 시간 오차 (초), 기본 5분
    )
}

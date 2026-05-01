package com.example.pickstranger.env

import com.example.pickstranger.RevEnvironment

object PickStrangerEnv {
    @JvmSynthetic
    fun create(id:String): RevEnvironment.Builder {
        return RevEnvironment.newBuilder()
            .setBackendServiceUrl("http://localhost:8080")
            .setProxyPort(9090)
            .setAiModuleGrpcUrl("http://lcalhost:5050")
            .setRiskScoreThreshold(0.5f)
            .setAiResponseTimeout(5000)
            .setBlacklistDuration(5000)
            .setVersion("v1.0.0")
    }
}
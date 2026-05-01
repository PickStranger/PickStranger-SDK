package com.example.pickstranger.env

import com.example.pickstranger.RevEnvironment
import com.example.pickstranger.RevEnvironmentKt
import com.example.pickstranger.revEnvironment

@JvmSynthetic
fun PickStrangerEnvDSL(env: RevEnvironmentKt.Dsl.() -> Unit) : RevEnvironment {
    return revEnvironment {
        backendServiceUrl = "http://localhost:8080"
        proxyPort = 9090
        aiModuleGrpcUrl = "http://localhost:5050"
        riskScoreThreshold=0.5f
        aiResponseTimeout=5000
        blacklistDuration=5000
        version="v1.0.0"

        env()
    }
}
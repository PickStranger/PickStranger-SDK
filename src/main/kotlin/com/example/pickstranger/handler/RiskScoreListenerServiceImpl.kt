package com.example.pickstranger.handler

import com.example.pickstranger.EvaluationAck
import com.example.pickstranger.LoginEvaluationEvent
import com.example.pickstranger.RiskScoreListenerServiceGrpcKt

internal class RiskScoreListenerServiceImpl(
    private val handler: RiskScoreHandler
) : RiskScoreListenerServiceGrpcKt.RiskScoreListenerServiceCoroutineImplBase() {

    override suspend fun onLoginEvaluated(request: LoginEvaluationEvent): EvaluationAck {
        handler.onEvaluated(request)
        return EvaluationAck.newBuilder().setReceived(true).build()
    }
}

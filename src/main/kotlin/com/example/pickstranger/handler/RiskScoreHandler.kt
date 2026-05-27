package com.example.pickstranger.handler

import com.example.pickstranger.LoginEvaluationEvent

fun interface RiskScoreHandler {
    fun onEvaluated(event: LoginEvaluationEvent)
}

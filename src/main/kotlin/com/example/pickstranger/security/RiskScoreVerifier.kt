package com.example.pickstranger.security

import com.example.pickstranger.riskscore.RiskScore

interface RiskScoreVerifier {
    @Throws(RiskScoreVerificationException::class)
    fun verify(headers: Map<String, String>, requestPath: String, method: String): RiskScore //기본 verify에 넣을 메서드

    fun supports(mode: String): Boolean //어떤 시그너처 인증방식을 쓸지 정하는거.
}
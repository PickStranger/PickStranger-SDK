package com.example.pickstranger.riskscore

data class RiskScore(
    val deviceId: String,//해싱된거
    val riskScore: Double,
    val timestamp: Long,
) {
    companion object {
        const val HEADER_DEVICE_ID = "X-Rev-Device-Id"
        const val HEADER_RISK_SCORE = "X-Rev-Risk-Score"
        const val HEADER_TIMESTAMP = "X-Rev-Timestamp"
        const val HEADER_SIGNATURE = "X-Rev-Signature"
        const val HEADER_SIGNATURE_TYPE = "X-REV-Signature_Type"
    }
}
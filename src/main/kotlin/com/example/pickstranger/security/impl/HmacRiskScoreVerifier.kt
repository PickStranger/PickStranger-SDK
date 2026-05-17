package com.example.pickstranger.security.impl

import com.example.pickstranger.riskscore.RiskScore
import com.example.pickstranger.security.ExpiredRiskScoreException
import com.example.pickstranger.security.InvalidRiskScoreSignatureException
import com.example.pickstranger.security.MissingRiskScoreHeadersException
import com.example.pickstranger.security.RiskScoreVerifier
import org.springframework.beans.factory.annotation.Value
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal class HmacRiskScoreVerifier(
    @Value("\${security.secret}") private val sharedSecret: String,
    @Value("\${security.timestamp.tolerance}") private val timestampToleranceMs: Long,
) : RiskScoreVerifier {

    override fun supports(mode: String): Boolean = mode.equals("HMAC", ignoreCase = true)

    override fun verify(
        headers: Map<String, String>,
        requestPath: String,
        method: String,
    ): RiskScore {
        val deviceId = headers[RiskScore.HEADER_DEVICE_ID] ?: throw MissingRiskScoreHeadersException()
        val scoreStr = headers[RiskScore.HEADER_RISK_SCORE] ?: throw MissingRiskScoreHeadersException()
        val timestampStr = headers[RiskScore.HEADER_TIMESTAMP] ?: throw MissingRiskScoreHeadersException()
        val signature = headers[RiskScore.HEADER_SIGNATURE] ?: throw MissingRiskScoreHeadersException()

        val score = scoreStr.toDoubleOrNull() ?: throw InvalidRiskScoreSignatureException()
        val timestamp = timestampStr.toLongOrNull() ?: throw InvalidRiskScoreSignatureException()

        if (System.currentTimeMillis() - timestamp > timestampToleranceMs) {
            throw ExpiredRiskScoreException()
        }

        val canonical = "$deviceId|$scoreStr|$timestampStr|$method|$requestPath"
        val expected = computeHmac(canonical)
        if (!constantTimeEquals(expected, signature)) {
            throw InvalidRiskScoreSignatureException()
        }

        return RiskScore(deviceId = deviceId, riskScore = score, timestamp = timestamp)
    }

    private fun computeHmac(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(sharedSecret.toByteArray(), "HmacSHA256"))
        return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].code xor b[i].code)
        return result == 0
    }
}
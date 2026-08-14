package com.example.pickstranger.security.impl

import com.example.pickstranger.riskscore.RiskScore
import com.example.pickstranger.security.ExpiredRiskScoreException
import com.example.pickstranger.security.InvalidRiskScoreSignatureException
import com.example.pickstranger.security.MissingRiskScoreHeadersException
import com.example.pickstranger.security.RiskScoreVerifier
import org.springframework.beans.factory.annotation.Value
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.security.Signature

internal class RsaRiskScoreVerifier(
    @Value("\${security.secret}") publicKeyPem: String,
    @Value("\${security.timestamp.tolerance}") private val timestampToleranceMs: Long,
) : RiskScoreVerifier {

    private val publicKey = run {
        val stripped = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val keyBytes = Base64.getDecoder().decode(stripped)
        KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBytes))
    }

    override fun supports(mode: String): Boolean = mode.equals("RSA", ignoreCase = true)

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
        val signatureBytes = runCatching { Base64.getDecoder().decode(signature) }
            .getOrElse { throw InvalidRiskScoreSignatureException() }

        val valid = Signature.getInstance("SHA256withRSA").run {
            initVerify(publicKey)
            update(canonical.toByteArray())
            verify(signatureBytes)
        }
        if (!valid) throw InvalidRiskScoreSignatureException()

        return RiskScore(deviceId = deviceId, riskScore = score, timestamp = timestamp)
    }
}
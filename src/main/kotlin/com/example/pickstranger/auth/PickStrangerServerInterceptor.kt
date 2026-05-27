package com.example.pickstranger.auth

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import kotlin.math.abs

internal class PickStrangerServerInterceptor(
    private val secret: String,
    private val timestampToleranceSeconds: Long
) : ServerInterceptor {

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val apiKey = headers.get(API_KEY_HEADER)
        if (apiKey != secret) {
            call.close(Status.UNAUTHENTICATED.withDescription("invalid credential"), Metadata())
            return object : ServerCall.Listener<ReqT>() {}
        }

        val timestamp = headers.get(TIMESTAMP_HEADER)?.toLongOrNull()
        if (timestamp == null) {
            call.close(Status.UNAUTHENTICATED.withDescription("missing timestamp"), Metadata())
            return object : ServerCall.Listener<ReqT>() {}
        }

        val nowSeconds = System.currentTimeMillis() / 1000
        if (abs(nowSeconds - timestamp) > timestampToleranceSeconds) {
            call.close(Status.UNAUTHENTICATED.withDescription("request expired"), Metadata())
            return object : ServerCall.Listener<ReqT>() {}
        }

        return next.startCall(call, headers)
    }

    companion object {
        val API_KEY_HEADER: Metadata.Key<String> =
            Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER)
        val TIMESTAMP_HEADER: Metadata.Key<String> =
            Metadata.Key.of("x-timestamp", Metadata.ASCII_STRING_MARSHALLER)
    }
}

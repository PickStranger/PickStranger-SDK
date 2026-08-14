package com.example.pickstranger.auth

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

internal class PickStrangerInterceptor(private val credential: PickStrangerCredential) : ClientInterceptor {

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                when (credential) {
                    is PickStrangerCredential.Jwt ->
                        headers.put(AUTHORIZATION_KEY, "Bearer ${credential.token}")
                    is PickStrangerCredential.ApiKey ->
                        headers.put(API_KEY_HEADER, credential.key)
                    else -> {}
                }
                super.start(responseListener, headers)
            }
        }
    }

    companion object {
        private val AUTHORIZATION_KEY = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
        private val API_KEY_HEADER = Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}

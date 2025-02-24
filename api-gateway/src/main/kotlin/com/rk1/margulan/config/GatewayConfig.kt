package com.rk1.margulan.config

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {

    @Bean
    fun modifyRequestHeaderGatewayFilterFactory(): ModifyRequestHeaderGatewayFilterFactory {
        return ModifyRequestHeaderGatewayFilterFactory()
    }
}

class ModifyRequestHeaderGatewayFilterFactory : AbstractGatewayFilterFactory<ModifyRequestHeaderGatewayFilterFactory.Config>(Config::class.java) {

    class Config {
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val authHeader = request.headers.getFirst("Authorization")
            val mutatedRequest = if (authHeader != null) {
                exchange.request.mutate()
                    .header("Authorization", authHeader)
                    .build()
            } else {
                request
            }
            chain.filter(exchange.mutate().request(mutatedRequest).build())
        }
    }
}
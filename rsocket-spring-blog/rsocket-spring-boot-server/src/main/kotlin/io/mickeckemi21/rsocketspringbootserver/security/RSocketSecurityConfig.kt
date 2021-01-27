package io.mickeckemi21.rsocketspringbootserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig {

    @Bean
    fun messageHandler(strategies: RSocketStrategies): RSocketMessageHandler =
        RSocketMessageHandler().apply {
            argumentResolverConfigurer.addCustomResolver(
                AuthenticationPrincipalArgumentResolver()
            )
            rSocketStrategies = strategies
        }

    @Bean
    fun authentication(): MapReactiveUserDetailsService {
        val user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("pass")
            .roles("USER")
            .build()

        val admin = User.withDefaultPasswordEncoder()
            .username("test")
            .password("pass")
            .roles("NONE")
            .build();

        return MapReactiveUserDetailsService(user, admin)
    }

    @Bean
    fun authorization(security: RSocketSecurity): PayloadSocketAcceptorInterceptor =
        security
            .authorizePayload { it.anyExchange().authenticated() }
            .simpleAuthentication(Customizer.withDefaults())
            .build()

}
package io.mickeckemi21.rsocketspringbaeldung.client.config

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.transport.netty.client.TcpClientTransport
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.MetadataExtractor
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.connectTcpAndAwait
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.util.MimeTypeUtils.APPLICATION_JSON
import org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE

@Configuration
class ClientConfig {

    @Bean
    fun rSocketRequester(
        @Value("\${spring.rsocket.server.host:localhost}") host: String,
        @Value("\${spring.rsocket.server.port:7000}") port: Int,
        rSocketStrategies: RSocketStrategies
    ): RSocketRequester = RSocketRequester.builder()
        .rsocketStrategies(rSocketStrategies)
        .connectTcp(host, port)
        .block()!!

}

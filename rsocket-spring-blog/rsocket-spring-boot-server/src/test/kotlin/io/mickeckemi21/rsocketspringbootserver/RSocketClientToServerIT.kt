package io.mickeckemi21.rsocketspringbootserver

import io.mickeckemi21.rsocketspringbootserver.controller.RSocketController.Companion.RESPONSE
import io.mickeckemi21.rsocketspringbootserver.controller.RSocketController.Companion.SERVER
import io.mickeckemi21.rsocketspringbootserver.model.Message
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.rsocket.context.LocalRSocketServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.connectTcpAndAwait
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata
import org.springframework.util.MimeTypeUtils
import org.springframework.util.MimeTypeUtils.*
import reactor.test.StepVerifier
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RSocketClientToServerIT {

    private lateinit var requester: RSocketRequester

    @BeforeAll
    fun setUpOnce(
        @Autowired builder: RSocketRequester.Builder,
        @LocalRSocketServerPort port: Int
    ) = runBlocking {
        val credentials = UsernamePasswordMetadata("user", "pass")
        val mimeType = parseMimeType(MESSAGE_RSOCKET_AUTHENTICATION.getString())
        requester = builder
            .setupData(UUID.randomUUID().toString())
            .setupMetadata(credentials, mimeType)
            .rsocketStrategies { it.encoder(SimpleAuthenticationEncoder()) }
            .connectTcpAndAwait("localhost", port)
    }

    @Test
    fun testRequestGetsResponse() {
        val result = requester
            .route("request.response")
            .data(Message("TEST", "REQUEST"))
            .retrieveMono(Message::class.java)
            .log()

        StepVerifier.create(result)
            .consumeNextWith { msg ->
                assertEquals(SERVER, msg.origin)
                assertEquals(RESPONSE, msg.interaction)
                assertEquals(0, msg.index)
            }
            .verifyComplete()
    }

    @AfterAll
    fun tearDownOnce() {
        requester.rsocket()!!.dispose()
    }

}
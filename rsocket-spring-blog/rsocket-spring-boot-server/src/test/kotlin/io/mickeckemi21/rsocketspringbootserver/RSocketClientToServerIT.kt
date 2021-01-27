package io.mickeckemi21.rsocketspringbootserver

import io.mickeckemi21.rsocketspringbootserver.controller.RSocketController.Companion.RESPONSE
import io.mickeckemi21.rsocketspringbootserver.controller.RSocketController.Companion.SERVER
import io.mickeckemi21.rsocketspringbootserver.model.Message
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.connectTcpAndAwait
import reactor.test.StepVerifier

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RSocketClientToServerIT {

    private lateinit var requester: RSocketRequester

    @BeforeAll
    fun setUpOnce(
        @Autowired builder: RSocketRequester.Builder,
        @Value("\${spring.rsocket.server.port:7000}") port: Int
    ) = runBlocking {
        requester = builder
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
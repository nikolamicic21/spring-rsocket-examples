package io.mickeckemi21.rsocketspringbootclient.shell

import io.mickeckemi21.rsocketspringbootclient.model.Message
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.connectTcpAndAwait
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class RSocketShellComponent(private val rSocketRequesterBuilder: RSocketRequester.Builder) {

    companion object {
        private val log = LoggerFactory.getLogger(RSocketShellComponent::class.java)
    }

    private val rSocketRequester = runBlocking {
        rSocketRequesterBuilder
            .connectTcpAndAwait("localhost", 7000)
    }

    @ShellMethod("Send one request, one response will be printed")
    fun requestResponse() {
        log.info("\nSending one request. Waiting for one response...")
        val response = rSocketRequester
            .route("request.response")
            .data(Message("Client", "Request"))
            .retrieveMono(Message::class.java)
            .block()
        log.info("\nResponse was: {}", response)
    }

}
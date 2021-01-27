package io.mickeckemi21.rsocketspringbootclient.shell

import io.mickeckemi21.rsocketspringbootclient.model.Message
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.connectTcpAndAwait
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.util.MimeTypeUtils.parseMimeType
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.*
import javax.annotation.PreDestroy

@ShellComponent
class RSocketShellComponent(
    private val rSocketRequesterBuilder: RSocketRequester.Builder,
    private val strategies: RSocketStrategies
) {

    companion object {
        private val SIMPLE_AUTH: MimeType = parseMimeType(MESSAGE_RSOCKET_AUTHENTICATION.string)
        private val CLIENT_ID = UUID.randomUUID().toString()

        private const val CLIENT = "CLIENT"
        private const val REQUEST = "REQUEST"
        private const val FIRE_AND_FORGET = "FIRE_AND_FORGET"
        private const val STREAM = "STREAM"

        private val log = LoggerFactory.getLogger(RSocketShellComponent::class.java)
    }

    private lateinit var rSocketRequester: RSocketRequester
    private lateinit var disposable: Disposable

    @ShellMethod("Login with your username and password")
    fun login(username: String, password: String) {
        val responder = RSocketMessageHandler
            .responder(strategies, ClientHandler())

        val user = UsernamePasswordMetadata(username, password)

        log.info("Connecting using client ID: $CLIENT_ID")
        runBlocking {
            rSocketRequester = rSocketRequesterBuilder
                .setupRoute("shell.client")
                .setupData(CLIENT_ID)
                .setupMetadata(user, SIMPLE_AUTH)
                .rsocketStrategies { it.encoder(SimpleAuthenticationEncoder()) }
                .rsocketConnector { it.acceptor(responder) }
                .connectTcpAndAwait("localhost", 7000)
        }
        rSocketRequester
            .rsocket()!!
            .onClose()
            .doOnError { log.warn("Connection CLOSED") }
            .doFinally { log.info("Client Disconnected") }
            .subscribe()
    }

    @PreDestroy
    @ShellMethod("Logout and close your connection")
    fun logout() {
        if (userIsLoggedIn()) {
            s()
            rSocketRequester.rsocket()!!.dispose()
            log.info("Logged out")
        }
    }

    private fun userIsLoggedIn(): Boolean =
        if (!this::rSocketRequester.isInitialized || rSocketRequester.rsocket()!!.isDisposed) {
            log.info("No connection. Did you login?");
            false
        } else {
            true
        }


    @ShellMethod("Stop streaming messages from he server")
    fun s() {
        if (this::disposable.isInitialized && userIsLoggedIn()) {
            disposable.dispose()
        }
    }

    @ShellMethod("Send one request, one response will be printed")
    fun requestResponse() {
        if (userIsLoggedIn()) {
            log.info("\nRequest-Response. Sending one request. Waiting for one response...")
            val response = rSocketRequester
                .route("request.response")
                .data(Message(CLIENT, REQUEST))
                .retrieveMono(Message::class.java)
                .block()
            log.info("\nResponse was: {}", response)
        }
    }

    @ShellMethod("Send one request. No response will be returned")
    fun fireAndForget() {
        if (userIsLoggedIn()) {
            log.info("\nFire-and-Forget. Sending one request. Expect no response (check server logs)...")
            rSocketRequester
                .route("fire.and.forget")
                .data(Message(CLIENT, FIRE_AND_FORGET))
                .send()
                .block()
        }
    }

    @ShellMethod("Send one request. Many responses (stream) will be printed")
    fun stream() {
        if (userIsLoggedIn()) {
            log.info("\nRequest-Stream. Sending one request. Waiting for unlimited responses (Stop process to quit)...");
            disposable = rSocketRequester
                .route("stream")
                .data(Message(CLIENT, STREAM))
                .retrieveFlux(Message::class.java)
                .subscribe { res -> log.info("Response received: {}", res) }
        }
    }

    @ShellMethod("Stream some settings to the server. Stream of responses will be printed")
    fun channel() {
        if (userIsLoggedIn()) {
            val setting1 = Mono.just(ofSeconds(1L))
            val setting2 = Mono.just(ofSeconds(3L)).delayElement(ofSeconds(5L))
            val setting3 = Mono.just(ofSeconds(5L)).delayElement(ofSeconds(15L))
            val settings = Flux.concat(setting1, setting2, setting3)
                .doOnNext { log.info("\nSending setting for ${it.seconds}-second interval.\n") }

            disposable = rSocketRequester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message::class.java)
                .subscribe { msg -> log.info("Received: $msg \n(Type 's' to stop.)") }
        }

    }

    class ClientHandler {

        companion object {
            private val log = LoggerFactory.getLogger(ClientHandler::class.java)
        }

        @MessageMapping("client.status")
        fun statusUpdate(status: String): Flux<String> {
            log.info("Connection $status")
            return Flux.interval(Duration.ofSeconds(5L))
                .map { Runtime.getRuntime().freeMemory().toString() }
        }

    }

}
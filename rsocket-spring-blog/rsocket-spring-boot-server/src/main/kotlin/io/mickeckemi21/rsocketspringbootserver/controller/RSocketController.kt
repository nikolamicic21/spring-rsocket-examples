package io.mickeckemi21.rsocketspringbootserver.controller

import io.mickeckemi21.rsocketspringbootserver.model.Message
import io.rsocket.RSocket
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Controller
class RSocketController {

    companion object {
        const val SERVER = "SERVER"
        const val RESPONSE = "RESPONSE"

        private val log = LoggerFactory.getLogger(RSocketController::class.java)
    }

    private val clients = ConcurrentHashMap.newKeySet<RSocketRequester>()

    @MessageMapping("request.response")
    fun requestResponse(request: Message): Message {
        log.info("Received request-response request: {}", request)
        // create a single Message and return it
        return Message(SERVER, RESPONSE)
    }

    @MessageMapping("fire.and.forget")
    fun fireAndForget(request: Message) {
        log.info("Received fire-and-forget request: {}", request)
    }

    @MessageMapping("stream")
    fun stream(request: Message): Flux<Message> {
        log.info("Received stream request: {}", request)
        return Flux.interval(Duration.ofSeconds(1L))
            .map { Message(SERVER, "STREAM", it) }
            .log()
    }

    @MessageMapping("channel")
    fun channel(settings: Flux<Duration>): Flux<Message> {
        log.info("Received channel request")
        return settings
            .doOnNext { setting ->
                log.info("\nFrequency setting is ${setting.seconds} second(s)\n")
            }.switchMap { setting ->
                Flux.interval(setting).map {
                    Message(SERVER, "CHANNEL", it)
                }
            }.log()
    }

    @ConnectMapping("shell.client")
    fun connectShellClientAndAskForTelemetry(
        requester: RSocketRequester,
        @Payload client: String
    ) {
        requester.rsocket()!!
            .onClose()
            .doFirst {
                log.info("Client: $client CONNECTED")
                clients.add(requester)
            }
            .doOnError {
                log.warn("Channel to client $client CLOSED")
            }
            .doFinally {
                clients.remove(requester)
                log.info("Client $client DISCONNECTED")
            }
            .subscribe()

        requester.route("client.status")
            .data("OPEN")
            .retrieveFlux(String::class.java)
            .doOnNext { log.info("Client: $client Free Memory: $it") }
            .subscribe()
    }


}


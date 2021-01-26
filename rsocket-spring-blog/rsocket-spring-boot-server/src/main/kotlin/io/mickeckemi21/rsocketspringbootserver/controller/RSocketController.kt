package io.mickeckemi21.rsocketspringbootserver.controller

import io.mickeckemi21.rsocketspringbootserver.model.Message
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
class RSocketController {

    companion object {
        private val log = LoggerFactory.getLogger(RSocketController::class.java)
    }

    @MessageMapping("request.response")
    fun requestResponse(request: Message): Message {
        log.info("Received request-response request: {}", request)
        // create a single Message and return it
        return Message("SERVER", "RESPONSE")
    }

    @MessageMapping("fire.and.forget")
    fun fireAndForget(request: Message) {
        log.info("Received fire-and-forget request: {}", request)
    }

    @MessageMapping("stream")
    fun stream(request: Message): Flux<Message> {
        log.info("Received stream request: {}", request)
        return Flux.interval(Duration.ofSeconds(1L))
            .map { Message("SERVER", "STREAM", it) }
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
                    Message("SERVER", "CHANNEL", it)
                }
            }.log()
    }


}


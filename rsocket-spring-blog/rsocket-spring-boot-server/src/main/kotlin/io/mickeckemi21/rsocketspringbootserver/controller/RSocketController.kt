package io.mickeckemi21.rsocketspringbootserver.controller

import io.mickeckemi21.rsocketspringbootserver.model.Message
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

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

}


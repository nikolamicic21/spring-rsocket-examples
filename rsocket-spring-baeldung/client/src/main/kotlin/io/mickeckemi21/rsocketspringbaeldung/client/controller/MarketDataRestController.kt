package io.mickeckemi21.rsocketspringbaeldung.client.controller

import io.mickeckemi21.rsocketspringbaeldung.model.MarketData
import io.mickeckemi21.rsocketspringbaeldung.model.MarketDataRequest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class MarketDataRestController
    (private val rSocketRequester: RSocketRequester) {

    @GetMapping("/current/{stock}")
    fun current(@PathVariable("stock") stock: String): Mono<MarketData> =
        rSocketRequester.route("current.market.data")
            .data(MarketDataRequest(stock))
            .retrieveMono(MarketData::class.java)

    @GetMapping("/collect")
    fun current(): Mono<Void> =
        rSocketRequester.route("collect.market.data")
            .data(getMarketData())
            .send()

    @GetMapping(value = ["/feed/{stock}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun feed(@PathVariable("stock") stock: String): Flux<MarketData> =
        rSocketRequester.route("feed.market.data")
//            .data(MarketDataRequest(stock))
            .data(stock)
            .retrieveFlux(MarketData::class.java)
            .log()

    private fun getMarketData(): MarketData =
        MarketData("X", (0..10).random())

}

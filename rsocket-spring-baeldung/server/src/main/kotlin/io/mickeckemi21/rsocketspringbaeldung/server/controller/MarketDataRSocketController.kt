package io.mickeckemi21.rsocketspringbaeldung.server.controller

import io.mickeckemi21.rsocketspringbaeldung.model.MarketData
import io.mickeckemi21.rsocketspringbaeldung.model.MarketDataRequest
import io.mickeckemi21.rsocketspringbaeldung.server.repository.MarketDataRepository
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class MarketDataRSocketController
    (private val marketDataRepository: MarketDataRepository) {

    @MessageMapping("current.market.data")
    fun currentMarketData(marketDataRequest: MarketDataRequest): Mono<MarketData> =
        marketDataRepository.getOne(marketDataRequest.stock!!)

    @MessageMapping("collect.market.data")
    fun collectMarketData(marketData: MarketData) {
        marketDataRepository.add(marketData)
    }

    @MessageMapping("feed.market.data")
    fun feedMarketData(marketDataRequest: MarketDataRequest): Flux<MarketData> =
        marketDataRepository.getAll(marketDataRequest.stock!!)

    @MessageExceptionHandler
    fun handleException(e: Exception): Mono<MarketData> {
        return Mono.just(MarketData.fromException(e))
    }

}

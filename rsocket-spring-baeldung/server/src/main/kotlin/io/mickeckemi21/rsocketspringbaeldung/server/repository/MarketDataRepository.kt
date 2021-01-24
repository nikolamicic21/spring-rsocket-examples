package io.mickeckemi21.rsocketspringbaeldung.server.repository

import io.mickeckemi21.rsocketspringbaeldung.model.MarketData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.stream.Stream

@Component
class MarketDataRepository {

    companion object {
        private const val BOUND = 100
        private val log = LoggerFactory.getLogger(MarketDataRepository::class.java)
    }

    fun getAll(stock: String): Flux<MarketData> = Flux
        .fromStream(Stream.generate { getMarketStockResponse(stock) })
        .log()
        .delayElements(Duration.ofSeconds(1L))

    fun getOne(stock: String): Mono<MarketData> = Mono
        .just(getMarketStockResponse(stock))
        .log()

    fun add(marketData: MarketData): Mono<Unit> {
        log.info("new market data: $marketData")
        return Mono.empty()
    }

    private fun getMarketStockResponse(stock: String): MarketData =
        MarketData(stock, (0..BOUND).random())

}

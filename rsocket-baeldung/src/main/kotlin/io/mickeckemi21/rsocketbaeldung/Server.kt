package io.mickeckemi21.rsocketbaeldung

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.server.TcpServerTransport
import org.reactivestreams.Publisher
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class Server {

    companion object {
        const val HOST = "localhost"
        const val TCP_PORT = 8091
        const val DATA_STREAM_NAME = "data"
    }

    private val server: Disposable
    val dataPublisher = DataPublisher()
    private val gameController = GameController("Server player")

    init {
        server = RSocketFactory.receive()
            .acceptor { _, _ -> Mono.just(RSocketImpl()) }
            .transport(TcpServerTransport.create(HOST, TCP_PORT))
            .start()
            .doOnNext { println("Server starter...") }
            .subscribe()
    }

    fun dispose() {
        dataPublisher.complete()
        server.dispose()
    }

    private inner class RSocketImpl : AbstractRSocket() {

        override fun requestResponse(payload: Payload): Mono<Payload> {
            return try {
                Mono.just(payload)
            } catch (e: Exception) {
                Mono.error(e)
            }
        }

        override fun fireAndForget(payload: Payload): Mono<Void> {
            return try {
                dataPublisher.publish(payload)
                Mono.empty()
            } catch (e: Exception) {
                Mono.error(e)
            }
        }

        override fun requestStream(payload: Payload): Flux<Payload> {
            return Flux.from(dataPublisher)
        }

        override fun requestChannel(payloads: Publisher<Payload>): Flux<Payload> {
            Flux.from(payloads)
                .subscribe(gameController::processPayload)

            return Flux.from(gameController)
        }
    }

}

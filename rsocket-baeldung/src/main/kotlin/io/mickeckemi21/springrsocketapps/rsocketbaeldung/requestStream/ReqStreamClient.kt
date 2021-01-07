package io.mickeckemi21.springrsocketapps.rsocketbaeldung.requestStream

import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server.Companion.DATA_STREAM_NAME
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server.Companion.HOST
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server.Companion.TCP_PORT
import io.rsocket.Payload
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Flux

class ReqStreamClient {

    private val socket = RSocketFactory.connect()
        .transport(TcpClientTransport.create(HOST, TCP_PORT))
        .start()
        .block()!!

    fun getDataStream(): Flux<Float> = socket
        .requestStream(DefaultPayload.create(DATA_STREAM_NAME))
        .map(Payload::getData)
        .map { it.float }
        .onErrorReturn(Float.NaN)

    fun dispose() {
        socket.dispose()
    }

}

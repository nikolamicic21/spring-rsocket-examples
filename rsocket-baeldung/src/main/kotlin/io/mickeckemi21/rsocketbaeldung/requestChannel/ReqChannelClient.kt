package io.mickeckemi21.rsocketbaeldung.requestChannel

import io.mickeckemi21.rsocketbaeldung.GameController
import io.mickeckemi21.rsocketbaeldung.Server.Companion.HOST
import io.mickeckemi21.rsocketbaeldung.Server.Companion.TCP_PORT
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport

class ReqChannelClient {

    private val socket: RSocket = RSocketFactory.connect()
        .transport(TcpClientTransport.create(HOST, TCP_PORT))
        .start()
        .block()!!

    private val gameController: GameController = GameController("Client Player")

    fun playGame() {
        socket.requestChannel(gameController)
            .doOnNext(gameController::processPayload)
            .blockLast()
    }

    fun dispose() {
        socket.dispose()
    }

}

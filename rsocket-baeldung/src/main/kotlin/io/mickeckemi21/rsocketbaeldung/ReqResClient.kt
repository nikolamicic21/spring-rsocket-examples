package io.mickeckemi21.rsocketbaeldung

import io.mickeckemi21.rsocketbaeldung.Server.Companion.HOST
import io.mickeckemi21.rsocketbaeldung.Server.Companion.TCP_PORT
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload

class ReqResClient {

    private val socket: RSocket = RSocketFactory.connect()
        .transport(TcpClientTransport.create(HOST, TCP_PORT))
        .start()
        .block()!!

    fun callBlocking(payload: String): String = socket
        .requestResponse(DefaultPayload.create(payload))
        .map(Payload::getDataUtf8)
        .block()!!

    fun dispose() {
        socket.dispose()
    }

}

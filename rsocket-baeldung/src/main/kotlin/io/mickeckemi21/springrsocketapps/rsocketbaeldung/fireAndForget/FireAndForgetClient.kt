package io.mickeckemi21.springrsocketapps.rsocketbaeldung.fireAndForget

import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server.Companion.HOST
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server.Companion.TCP_PORT
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Flux
import java.nio.ByteBuffer
import java.time.Duration
import java.util.ArrayList

class FireAndForgetClient {

    companion object {
        const val DATA_LENGTH = 30
    }

    private val socket: RSocket = RSocketFactory.connect()
        .transport(TcpClientTransport.create(HOST, TCP_PORT))
        .start()
        .block()!!

    val data: List<Float> = generateData()

    private fun generateData(): List<Float> {
        val dataList: MutableList<Float> = ArrayList<Float>(DATA_LENGTH)
        var velocity = 0f
        for (i in 0 until DATA_LENGTH) {
            velocity += Math.random().toFloat()
            dataList.add(velocity)
        }

        return dataList
    }

    fun sendData() {
        Flux.interval(Duration.ofMillis(50))
            .take(data.size.toLong())
            .map(this::createFloatPayload)
            .flatMap(socket::fireAndForget)
            .blockLast()
    }

    private fun createFloatPayload(index: Long): Payload {
        val velocity: Float = data[index.toInt()]
        val buffer = ByteBuffer.allocate(4).putFloat(velocity)
        buffer.rewind()

        return DefaultPayload.create(buffer)
    }

    fun dispose() {
        socket.dispose()
    }

}

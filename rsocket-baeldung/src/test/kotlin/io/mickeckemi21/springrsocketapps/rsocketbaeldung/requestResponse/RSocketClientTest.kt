package io.mickeckemi21.springrsocketapps.rsocketbaeldung.requestResponse

import io.mickeckemi21.springrsocketapps.rsocketbaeldung.Server
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.fireAndForget.FireAndForgetClient
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.requestChannel.ReqChannelClient
import io.mickeckemi21.springrsocketapps.rsocketbaeldung.requestStream.ReqStreamClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RSocketClientTest {

    lateinit var server: Server

    @BeforeEach
    internal fun setUp() {
        server = Server()
    }

    @Test
    fun `when sending a string - then receive the same string`() {
        val reqResClient = ReqResClient()
        val payload = "Hello RSocket";

        assertEquals(payload, reqResClient.callBlocking(payload))
        reqResClient.dispose()
    }

    @Test
    fun `when sending stream - then receive the same stream`() {
        val fireNForgetClient = FireAndForgetClient()
        val reqStreamClient = ReqStreamClient()

        val data = fireNForgetClient.data
        val dataReceived: MutableList<Float> = mutableListOf()

        reqStreamClient.getDataStream()
            .index()
            .subscribe(
                { tuple ->
                    assertEquals(data[tuple.t1.toInt()], tuple.t2)
                    dataReceived.add(tuple.t2)
                },
                { err ->
                    println(err.message)
                }
            )

        fireNForgetClient.sendData()

        fireNForgetClient.dispose()
        reqStreamClient.dispose()

        assertEquals(data.size, dataReceived.size)
    }

    @Test
    fun `when running channel game - then log the results`() {
        val client = ReqChannelClient()
        client.playGame()
        client.dispose()
    }

    @AfterEach
    internal fun tearDown() {
        server.dispose()
    }
}

package io.mickeckemi21.rsocketbaeldung

import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import kotlin.math.ceil

class GameController(private val playerName: String) : Publisher<Payload> {

    private lateinit var subscriber: Subscriber<in Payload>
    private var truce: Boolean = false
    private val shots: List<Long> = generateShots()

    companion object {
        const val SHOT_COUNT = 10
    }

    override fun subscribe(s: Subscriber<in Payload>) {
        subscriber = s
        fireAtWill()
    }

    private fun fireAtWill() = runBlocking {
        shots.forEach {
            delay(it)
            if (truce) return@forEach
            println("$playerName: bang!")
            subscriber.onNext(DefaultPayload.create("bang!"))
        }

        if (!truce) {
            println("$playerName: I give up!")
            subscriber.onNext(DefaultPayload.create("I give up"))
        }

        subscriber.onComplete()
    }

    private fun generateShots(): List<Long> = (1..SHOT_COUNT)
        .map { ceil(Math.random() * 1000).toLong() }
        .toList()


    fun processPayload(payload: Payload) {
        when (payload.dataUtf8!!) {
            "bang!" -> {
                val result = if (Math.random() < 0.5) "Haha missed!" else "Ow!"
                println("$playerName: $result")
            }
            "I give up" -> {
                truce = true
                println("$playerName: OK, truce")
            }
        }
    }


}

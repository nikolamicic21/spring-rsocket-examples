package io.mickeckemi21.rsocketbaeldung

import io.rsocket.Payload
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

class DataPublisher : Publisher<Payload> {

    private lateinit var subscriber: Subscriber<in Payload>

    override fun subscribe(s: Subscriber<in Payload>) {
        subscriber = s
    }

    fun publish(payload: Payload) {
        if (this::subscriber.isInitialized) {
            subscriber.onNext(payload)
        }
    }

    fun complete() {
        if (this::subscriber.isInitialized) {
            subscriber.onComplete()
        }
    }

}

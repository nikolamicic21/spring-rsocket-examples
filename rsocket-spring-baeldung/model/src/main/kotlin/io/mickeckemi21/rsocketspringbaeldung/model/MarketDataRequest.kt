package io.mickeckemi21.rsocketspringbaeldung.model

class MarketDataRequest() {

    constructor(stock: String) : this() {
        this.stock = stock
    }

    var stock: String? = null

}

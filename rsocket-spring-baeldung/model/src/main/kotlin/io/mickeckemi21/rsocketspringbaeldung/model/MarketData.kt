package io.mickeckemi21.rsocketspringbaeldung.model

class MarketData() {

    companion object {
        fun fromException(e: Exception): MarketData = MarketData().apply {
            stock = e.message
        }
    }

    constructor(stock: String, price: Int) : this() {
        this.stock = stock
        this.currentPrice = price
    }

    var stock: String? = null
    var currentPrice: Int? = null

    override fun toString(): String {
        return "MarketData(stock=$stock, currentPrice=$currentPrice)"
    }

}

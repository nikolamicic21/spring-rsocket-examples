package io.mickeckemi21.rsocketspringbootclient.model

import java.time.Instant

class Message() {

    var origin: String? = null
    var interaction: String? = null
    var index: Long? = 0L
    var created: Long = Instant.now().epochSecond

    constructor(
        origin: String,
        interaction: String
    ): this() {
        this.origin = origin
        this.interaction = interaction
    }

    constructor(
        origin: String,
        interaction: String,
        index: Long
    ): this(origin, interaction) {
        this.index = index
    }

    constructor(
        origin: String,
        interaction: String,
        index: Long,
        created: Long
    ): this(origin, interaction, index) {
        this.created = created
    }

    override fun toString(): String =
        "Message " +
                "[origin: $origin, " +
                "interaction: $interaction, " +
                "index: $index, " +
                "created: $created]"

}

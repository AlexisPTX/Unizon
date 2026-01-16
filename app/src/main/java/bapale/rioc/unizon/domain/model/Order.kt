package bapale.rioc.unizon.domain.model

data class Order(
    val orderId: Long,
    val timestamp: Long,
    val totalPrice: Double
)
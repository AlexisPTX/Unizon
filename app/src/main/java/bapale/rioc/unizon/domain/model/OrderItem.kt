package bapale.rioc.unizon.domain.model

data class OrderItem(
    val orderItemId: Long,
    val parentOrderId: Long,
    val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int
)
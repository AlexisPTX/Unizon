package bapale.rioc.unizon.domain.model

data class OrderWithItems(
    val order: Order,
    val items: List<OrderItem>
)
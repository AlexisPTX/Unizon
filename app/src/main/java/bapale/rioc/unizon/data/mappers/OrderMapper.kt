package bapale.rioc.unizon.data.mappers

import bapale.rioc.unizon.data.Order as DataOrder
import bapale.rioc.unizon.data.OrderItem as DataOrderItem
import bapale.rioc.unizon.data.OrderWithItems as DataOrderWithItems
import bapale.rioc.unizon.domain.model.Order
import bapale.rioc.unizon.domain.model.OrderItem
import bapale.rioc.unizon.domain.model.OrderWithItems

fun DataOrderWithItems.toDomain(): OrderWithItems {
    return OrderWithItems(
        order = this.order.toDomain(),
        items = this.items.map { it.toDomain() }
    )
}

fun DataOrder.toDomain(): Order {
    return Order(
        orderId = this.orderId,
        timestamp = this.timestamp,
        totalPrice = this.totalPrice
    )
}

fun DataOrderItem.toDomain(): OrderItem {
    return OrderItem(
        orderItemId = this.orderItemId,
        parentOrderId = this.parentOrderId,
        productId = this.productId,
        title = this.title,
        price = this.price,
        image = this.image,
        quantity = this.quantity
    )
}
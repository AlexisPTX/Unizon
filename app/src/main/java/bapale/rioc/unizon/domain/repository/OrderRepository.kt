package bapale.rioc.unizon.domain.repository

import bapale.rioc.unizon.domain.model.OrderWithItems
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrdersWithItems(): Flow<List<OrderWithItems>>
}
package bapale.rioc.unizon.data.repository

import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.mappers.toDomain
import bapale.rioc.unizon.domain.model.OrderWithItems
import bapale.rioc.unizon.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    db: AppDatabase
) : OrderRepository {

    private val orderDao = db.orderDao()

    override fun getOrdersWithItems(): Flow<List<OrderWithItems>> {
        return orderDao.getOrdersWithItems().map { list ->
            list.map { it.toDomain() }
        }
    }
}
package bapale.rioc.unizon.data.repository

import bapale.rioc.unizon.data.mappers.toDomain
import bapale.rioc.unizon.data.remote.api.FakeStoreApiService
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val api: FakeStoreApiService
) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        return api.getProducts().map { it.toDomain() }
    }

    override suspend fun getProductById(id: Int): Product {
        return api.getProductById(id).toDomain()
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return api.getProductsByCategory(category).map { it.toDomain() }
    }

    override suspend fun getCategories(): List<String> {
        return api.getCategories()
    }
}
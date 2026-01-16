package bapale.rioc.unizon.domain.repository

import bapale.rioc.unizon.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(id: Int): Product
    suspend fun getProductsByCategory(category: String): List<Product>
    suspend fun getCategories(): List<String>
}
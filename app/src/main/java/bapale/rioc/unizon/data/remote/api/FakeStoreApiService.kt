package bapale.rioc.unizon.data.remote.api

import bapale.rioc.unizon.data.remote.dto.ProductDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FakeStoreApiService {
    @GET("/products")
    suspend fun getProducts(): List<ProductDto>

    @GET("/products/categories")
    suspend fun getCategories(): List<String>

    @GET("/products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): List<ProductDto>

    @GET("/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto
}
package bapale.rioc.unizon.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Rating(
    val rate: Double,
    val count: Int
)
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating
)

interface FakeStoreApiService {
    @GET("/products")
    suspend fun getProducts(): List<Product>

    @GET("/products/categories")
    suspend fun getCategories(): List<String>

    @GET("/products/category/{category}")
    suspend fun getProductsByCategory(
        @retrofit2.http.Path("category") category: String
    ): List<Product>

    @GET("/products/{id}")
    suspend fun getProductById(@retrofit2.http.Path("id") id: Int): Product
}

object RetrofitInstance {

    private val fakeStoreRetrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fakeStoreService: FakeStoreApiService = fakeStoreRetrofit.create(FakeStoreApiService::class.java)
}

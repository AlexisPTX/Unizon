package bapale.rioc.unizon.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)

interface FakeStoreApiService {
    @GET("/products")
    suspend fun getProducts(): List<Product>
}

object RetrofitInstance {

    private val fakeStoreRetrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fakeStoreService: FakeStoreApiService = fakeStoreRetrofit.create(FakeStoreApiService::class.java)
}

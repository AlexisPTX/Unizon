package bapale.rioc.unizon.di

import android.content.Context
import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.remote.api.FakeStoreApiService
import bapale.rioc.unizon.data.repository.FavoriteRepositoryImpl
import bapale.rioc.unizon.data.repository.OrderRepositoryImpl
import bapale.rioc.unizon.data.repository.ProductRepositoryImpl
import bapale.rioc.unizon.domain.repository.FavoriteRepository
import bapale.rioc.unizon.domain.repository.OrderRepository
import bapale.rioc.unizon.domain.repository.ProductRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    private val fakeStoreApi: FakeStoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreApiService::class.java)
    }

    @Volatile
    private var appDatabase: AppDatabase? = null

    private fun provideDatabase(context: Context): AppDatabase {
        return appDatabase ?: synchronized(this) {
            appDatabase ?: AppDatabase.getDatabase(context).also { appDatabase = it }
        }
    }

    fun provideProductRepository(): ProductRepository {
        return ProductRepositoryImpl(fakeStoreApi)
    }

    fun provideOrderRepository(context: Context): OrderRepository {
        return OrderRepositoryImpl(provideDatabase(context))
    }

    fun provideFavoriteRepository(context: Context): FavoriteRepository {
        return FavoriteRepositoryImpl(provideDatabase(context))
    }
}
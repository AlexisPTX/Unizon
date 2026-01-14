package bapale.rioc.unizon.data

import android.content.Context
import androidx.room.Transaction
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Embedded
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- Cart Entities ---
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "quantity") val quantity: Int
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItem)

    @Delete
    suspend fun deleteItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

// --- Favorites Entities ---
@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey val productId: Int
)

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteItem)

    @Query("DELETE FROM favorite_items WHERE productId = :productId")
    suspend fun removeFavorite(productId: Int)

    @Query("SELECT * FROM favorite_items")
    fun getAllFavorites(): Flow<List<FavoriteItem>>
}

// --- Order Entities ---
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,
    val timestamp: Long,
    val totalPrice: Double
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val orderItemId: Long = 0,
    val parentOrderId: Long, // Foreign key to Order
    val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int
)

data class OrderWithItems(
    @Embedded val order: Order,
    @androidx.room.Relation(
        parentColumn = "orderId",
        entityColumn = "parentOrderId"
    )
    val items: List<OrderItem>
)

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Transaction
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrdersWithItems(): Flow<List<OrderWithItems>>
}


@Database(entities = [CartItem::class, Order::class, OrderItem::class, FavoriteItem::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "unizon_database")
                .fallbackToDestructiveMigration() // For simplicity, otherwise need a migration plan
                .build().also { INSTANCE = it } }
    }
}
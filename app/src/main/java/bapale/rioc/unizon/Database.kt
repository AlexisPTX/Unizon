package bapale.rioc.unizon

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prenom: String,
    val nom: String,
    val birthDate: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>

    @Insert
    suspend fun insert(user: User)
}

@Entity(tableName = "favorite_jokes")
data class FavoriteJoke(
    @PrimaryKey val id: Int,
    val setup: String,
    val punchline: String
)

@Dao
interface JokeDao {
    @Query("SELECT * FROM favorite_jokes")
    fun getAllFavorites(): Flow<List<FavoriteJoke>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(joke: FavoriteJoke)

    @Delete
    suspend fun delete(joke: FavoriteJoke)
}

@Database(entities = [User::class, FavoriteJoke::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jokeDao(): JokeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package bapale.rioc.unizon.data.repository

import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.FavoriteItem
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(db: AppDatabase) : FavoriteRepository {

    private val dao = db.favoriteDao()

    override fun getFavoriteProductIds(): Flow<Set<Int>> {
        return dao.getAllFavorites().map { list ->
            list.map { it.productId }.toSet()
        }
    }

    override suspend fun addFavorite(product: Product) {
        dao.addFavorite(FavoriteItem(productId = product.id))
    }

    override suspend fun removeFavorite(product: Product) {
        dao.removeFavorite(product.id)
    }

    override fun getFavoritesCount(): Flow<Int> {
        return getFavoriteProductIds().map { it.size }
    }
}
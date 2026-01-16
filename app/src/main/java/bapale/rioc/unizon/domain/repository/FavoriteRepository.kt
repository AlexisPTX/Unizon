package bapale.rioc.unizon.domain.repository

import bapale.rioc.unizon.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteProductIds(): Flow<Set<Int>>
    suspend fun addFavorite(product: Product)
    suspend fun removeFavorite(product: Product)
    fun getFavoritesCount(): Flow<Int>
}
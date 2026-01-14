package bapale.rioc.unizon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.api.Product
import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.FavoriteItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()

    val favoriteProductIds: StateFlow<Set<Int>> = favoriteDao.getAllFavorites()
        .map { it.map { favorite -> favorite.productId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val favoritesCount: StateFlow<Int> = favoriteProductIds.map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            if (favoriteProductIds.value.contains(product.id)) {
                favoriteDao.removeFavorite(product.id)
            } else {
                favoriteDao.addFavorite(FavoriteItem(productId = product.id))
            }
        }
    }
}
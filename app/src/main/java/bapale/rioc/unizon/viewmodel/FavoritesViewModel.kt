package bapale.rioc.unizon.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    class Factory(private val favoriteRepository: FavoriteRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(favoriteRepository) as T
        }
    }

    val favoriteProductIds: StateFlow<Set<Int>> = favoriteRepository.getFavoriteProductIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val favoritesCount: StateFlow<Int> = favoriteRepository.getFavoritesCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            if (favoriteProductIds.value.contains(product.id)) {
                favoriteRepository.removeFavorite(product)
            } else {
                favoriteRepository.addFavorite(product)
            }
        }
    }
}
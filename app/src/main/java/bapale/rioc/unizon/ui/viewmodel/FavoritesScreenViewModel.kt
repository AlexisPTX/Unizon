package bapale.rioc.unizon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.FavoriteRepository
import bapale.rioc.unizon.domain.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class FavoritesScreenState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true
)

class FavoritesScreenViewModel(
    productRepository: ProductRepository,
    favoriteRepository: FavoriteRepository
) : ViewModel() {

    // Combine le flux des IDs favoris avec un appel unique pour récupérer tous les produits
    val state: StateFlow<FavoritesScreenState> = combine(
        favoriteRepository.getFavoriteProductIds(),
        flow { emit(productRepository.getProducts()) } // Récupère tous les produits une seule fois
    ) { favoriteIds, allProducts ->
        FavoritesScreenState(
            products = allProducts.filter { it.id in favoriteIds },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesScreenState(isLoading = true)
    )
}
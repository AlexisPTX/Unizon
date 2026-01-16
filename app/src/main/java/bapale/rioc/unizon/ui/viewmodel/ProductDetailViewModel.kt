package bapale.rioc.unizon.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.ProductRepository
import kotlinx.coroutines.launch

data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(ProductDetailState())
        private set

    init {
        // Récupère l'ID du produit passé via la navigation
        val productId: Int? = savedStateHandle["productId"]
        productId?.let { loadProduct(it) }
    }

    private fun loadProduct(productId: Int) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val product = productRepository.getProductById(productId)
                state = state.copy(product = product, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(error = "Failed to load product details.", isLoading = false)
            }
        }
    }
}
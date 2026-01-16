package bapale.rioc.unizon.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.domain.repository.ProductRepository
import bapale.rioc.unizon.ui.screen.SortOption
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class ProductsState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val sortOption: SortOption = SortOption.NONE,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    var state by mutableStateOf(ProductsState())
        private set

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCategories()
        loadProducts()
    }

    fun onCategorySelected(category: String?) {
        state = state.copy(selectedCategory = category)
        loadProducts()
    }

    fun onSortSelected(sortOption: SortOption) {
        state = state.copy(sortOption = sortOption)
    }

    fun loadProducts() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val products = state.selectedCategory?.let {
                    productRepository.getProductsByCategory(it)
                } ?: productRepository.getProducts()
                state = state.copy(products = products)
            } catch (e: Exception) {
                val errorMessage = "Error loading products"
                state = state.copy(error = errorMessage)
                _uiEvent.send(errorMessage)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = productRepository.getCategories()
                state = state.copy(categories = categories)
            } catch (e: Exception) {
                _uiEvent.send("Error loading categories")
            }
        }
    }

    fun getDisplayedProducts(): List<Product> {
        return when (state.sortOption) {
            SortOption.PRICE_ASC -> state.products.sortedBy { it.price }
            SortOption.PRICE_DESC -> state.products.sortedByDescending { it.price }
            SortOption.RATING_DESC -> state.products.sortedByDescending { it.rating.rate }
            SortOption.NONE -> state.products
        }
    }
}
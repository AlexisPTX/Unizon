package bapale.rioc.unizon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.api.Product
import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.CartItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application).cartDao()

    val cartItems: StateFlow<List<CartItem>> = cartDao.getAllItems()
        .stateIn(
            scope = viewModelScope,
            // évite de spam la db de requêtes en faisant des aller-retours panier <-> produits
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cartTotalPrice: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val cartItemCount: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val existingItem = cartItems.value.find { it.productId == product.id }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                cartDao.insertItem(updatedItem)
            } else {
                val newItem = CartItem(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    image = product.image,
                    quantity = 1
                )
                cartDao.insertItem(newItem)
            }
        }
    }

    fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            val existingItem = cartItems.value.find { it.productId == product.id }
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
                    cartDao.insertItem(updatedItem)
                } else {
                    cartDao.deleteItem(existingItem)
                }
            }
        }
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) cartDao.insertItem(item.copy(quantity = newQuantity)) else cartDao.deleteItem(item)
        }
    }

    fun removeItemFromCart(item: CartItem) {
        viewModelScope.launch {
            cartDao.deleteItem(item)
        }
    }
}
package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bapale.rioc.unizon.api.Product
import bapale.rioc.unizon.api.RetrofitInstance
import bapale.rioc.unizon.viewmodel.CartViewModel
import bapale.rioc.unizon.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val favoriteIds by favoritesViewModel.favoriteProductIds.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    // This screen needs its own product list, fetched from the API
    var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            allProducts = RetrofitInstance.fakeStoreService.getProducts()
        } catch (_: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    val favoriteProducts = remember(allProducts, favoriteIds) {
        allProducts.filter { it.id in favoriteIds }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (favoriteProducts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You have no favorite products.", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favoriteProducts, key = { it.id }) { product ->
                val quantityInCart = cartItems.find { it.productId == product.id }?.quantity ?: 0
                ProductItem(
                    product = product,
                    quantityInCart = quantityInCart,
                    isFavorite = true, // All items on this screen are favorites
                    onToggleFavorite = { favoritesViewModel.toggleFavorite(product) },
                    onAddToCart = { cartViewModel.addToCart(product) },
                    onRemoveFromCart = { cartViewModel.decreaseQuantity(product) },
                    onClick = { navController.navigate("product_detail/${product.id}") }
                )
            }
        }
    }
}
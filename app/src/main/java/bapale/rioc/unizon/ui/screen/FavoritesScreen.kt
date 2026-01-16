package bapale.rioc.unizon.ui.screen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bapale.rioc.unizon.di.AppModule
import bapale.rioc.unizon.ui.viewmodel.FavoritesScreenViewModel
import bapale.rioc.unizon.viewmodel.CartViewModel
import bapale.rioc.unizon.viewmodel.FavoritesViewModel
import androidx.lifecycle.ViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel, // Pour gérer l'action de mise en favori
    favoritesScreenViewModel: FavoritesScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val context = (navController.context as android.app.Activity).applicationContext
                return FavoritesScreenViewModel(
                    AppModule.provideProductRepository(),
                    AppModule.provideFavoriteRepository(context)
                ) as T
            }
        }
    )
) {
    val state by favoritesScreenViewModel.state.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You have no favorite products.", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.products, key = { it.id }) { product ->
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
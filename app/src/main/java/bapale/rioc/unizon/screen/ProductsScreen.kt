package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.Arrangement
import bapale.rioc.unizon.api.RetrofitInstance
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bapale.rioc.unizon.api.Product
import bapale.rioc.unizon.viewmodel.CartViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(cartViewModel: CartViewModel) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val cartItems by cartViewModel.cartItems.collectAsState()

    fun loadProducts(category: String? = selectedCategory) {
        scope.launch {
            isLoading = true
            error = null
            try {
                // Charger les produits en fonction de la catégorie sélectionnée
                products = if (category == null) {
                    RetrofitInstance.fakeStoreService.getProducts()
                } else {
                    RetrofitInstance.fakeStoreService.getProductsByCategory(
                        category
                    )
                }
            } catch (e: Exception) {
                val errorMessage = "Error loading products"
                error = errorMessage
                snackbarHostState.showSnackbar(errorMessage)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCategories() {
        scope.launch {
            try {
                categories = RetrofitInstance.fakeStoreService.getCategories()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error loading categories")
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCategories()
        loadProducts()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CategoriesRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    loadProducts(category)
                }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading && products.isEmpty()) {
                    CircularProgressIndicator()
                } else if (error != null && products.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { loadProducts() }) {
                            Text("Try again")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            val quantityInCart = cartItems.find { it.productId == product.id }?.quantity ?: 0
                            ProductItem(
                                product = product,
                                quantityInCart = quantityInCart,
                                onAddToCart = { cartViewModel.addToCart(product) },
                                onRemoveFromCart = { cartViewModel.decreaseQuantity(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    // La liste complète inclut une option pour tout afficher
    val displayCategories = listOf("all") + categories

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(displayCategories) { category ->
            val isSelected = (category == "all" && selectedCategory == null) || category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newSelection = if (category == "all") null else category
                    onCategorySelected(newSelection)
                },
                label = { Text(text = category) }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.image,
                contentDescription = "Image of product ${product.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(48.dp) // Hauteur fixe pour l'alignement
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${product.price} €",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (quantityInCart == 0) {
                        Button(onClick = onAddToCart) {
                            Text("Add")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = onRemoveFromCart, modifier = Modifier.size(36.dp)) {
                                Icon(if (quantityInCart == 1) Icons.Default.Delete else Icons.Default.Remove, "Decrease")
                            }
                            Text(text = "$quantityInCart", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            IconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Add, "Increase")
                            }
                        }
                    }
                }
            }
        }
    }
}
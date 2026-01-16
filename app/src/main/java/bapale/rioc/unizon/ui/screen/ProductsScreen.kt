package bapale.rioc.unizon.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import bapale.rioc.unizon.di.AppModule
import bapale.rioc.unizon.domain.model.Product
import bapale.rioc.unizon.viewmodel.FavoritesViewModel
import bapale.rioc.unizon.viewmodel.CartViewModel
import bapale.rioc.unizon.ui.viewmodel.ProductDetailViewModel
import bapale.rioc.unizon.ui.viewmodel.ProductsViewModel
import coil.compose.AsyncImage
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

enum class SortOption(val displayName: String) {
    NONE("Default"),
    PRICE_ASC("Price ↑"),
    PRICE_DESC("Price ↓"),
    RATING_DESC("Rating: High to Low")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController,
    productsViewModel: ProductsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductsViewModel(AppModule.provideProductRepository()) as T
            }
        }
    )
) {
    val state = productsViewModel.state
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val cartItems by cartViewModel.cartItems.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteProductIds.collectAsState()
    val listState = rememberLazyListState()
    val displayedProducts = productsViewModel.getDisplayedProducts()

    LaunchedEffect(displayedProducts) {
        if (displayedProducts.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) { // The main column for the screen
            FilterBar(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { productsViewModel.onCategorySelected(it) },
                onSortSelected = { productsViewModel.onSortSelected(it) }
            )
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { productsViewModel.loadProducts() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = if (displayedProducts.isEmpty() && !state.isLoading) Arrangement.Center else Arrangement.spacedBy(16.dp),
                    horizontalAlignment = if (displayedProducts.isEmpty() && !state.isLoading) Alignment.CenterHorizontally else Alignment.Start
                ) {
                    if (displayedProducts.isEmpty() && !state.isLoading) {
                        item {
                            if (state.error != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            } else {
                                Text("No products found.")
                            }
                        }
                    } else {
                        items(displayedProducts, key = { it.id }) { product ->
                            val quantityInCart = cartItems.find { it.productId == product.id }?.quantity ?: 0
                            val isFavorite = favoriteIds.contains(product.id)
                            ProductItem(
                                product = product,
                                quantityInCart = quantityInCart,
                                isFavorite = isFavorite,
                                onToggleFavorite = { favoritesViewModel.toggleFavorite(product) },
                                onAddToCart = { cartViewModel.addToCart(product) },
                                onRemoveFromCart = { cartViewModel.decreaseQuantity(product) },
                                onClick = { navController.navigate("product_detail/${product.id}") }
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
fun FilterBar(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Categories Chips
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displayCategories = listOf("all") + categories
            items(displayCategories) { category ->
                val isSelected = (category == "all" && selectedCategory == null) || category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (category == "all") null else category
                        onCategorySelected(newSelection)
                    },
                    label = { Text(text = if (category == "all") "All" else category.replaceFirstChar { it.uppercaseChar() }) }
                )
            }
        }

        // Sort Button
        var menuExpanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort products")
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(text = { Text(option.displayName) }, onClick = { onSortSelected(option); menuExpanded = false })
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    quantityInCart: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.image,
                    contentDescription = "Image of product ${product.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(48.dp) // Hauteur fixe pour l'alignement
                )
                RatingStars(
                    rate = product.rating.rate,
                    count = product.rating.count,
                    modifier = Modifier.padding(vertical = 4.dp)
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
                            Text("Add to cart")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: ProductDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return ProductDetailViewModel(AppModule.provideProductRepository(), savedStateHandle) as T
            }
        }
    )
) {
    val state = viewModel.state

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(state.error, color = MaterialTheme.colorScheme.error)
        } else if (state.product != null) {
            val product = state.product
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = "Image of product ${product.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(product.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                RatingStars(
                    rate = product.rating.rate,
                    count = product.rating.count,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${product.price} €",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Description", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(product.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}


@Composable
fun RatingStars(
    rate: Double,
    count: Int,
    modifier: Modifier
) {
    val filledStars = rate.toInt()
    val hasHalfStar = (rate - filledStars) >= 0.5

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5)
        {
            when {
                i <= filledStars -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                i == filledStars + 1 && hasHalfStar -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${"%.1f".format(rate)} ($count)",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
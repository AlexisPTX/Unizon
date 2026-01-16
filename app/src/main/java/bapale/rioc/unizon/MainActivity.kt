package bapale.rioc.unizon

import bapale.rioc.unizon.ui.screen.CartScreen
import bapale.rioc.unizon.ui.screen.CheckoutScreen
import bapale.rioc.unizon.ui.screen.OrderHistoryScreen
import bapale.rioc.unizon.ui.screen.ProductDetailScreen
import bapale.rioc.unizon.ui.screen.ProductsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import bapale.rioc.unizon.ui.theme.UnizonTheme
import bapale.rioc.unizon.viewmodel.CartViewModel
import androidx.navigation.NavType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import bapale.rioc.unizon.ui.screen.FavoritesScreen
import bapale.rioc.unizon.di.AppModule
import bapale.rioc.unizon.viewmodel.FavoritesViewModel
import androidx.lifecycle.ViewModel

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Products : BottomNavItem("products", Icons.Default.Storefront, "Products")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object History : BottomNavItem("order_history", Icons.Default.History, "History")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UnizonTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val cartViewModel: CartViewModel = viewModel()
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModel.Factory(
                        AppModule.provideFavoriteRepository(applicationContext)
                    )
                )

                val cartItemsCount by cartViewModel.cartItemCount.collectAsState()
                val favoritesCount by favoritesViewModel.favoritesCount.collectAsState()

                val topBarTitle = when {
                    currentRoute == "products" -> "Products"
                    currentRoute == "cart" -> "Cart"
                    currentRoute == "checkout" -> "Checkout"
                    currentRoute == "order_history" -> "Order History"
                    currentRoute == "favorites" -> "Favorites"
                    currentRoute?.startsWith("product_detail") == true -> "Product Detail"
                    else -> "Unizon Store"
                }

                val bottomNavItems = listOf(
                    BottomNavItem.Products,
                    BottomNavItem.Favorites,
                    BottomNavItem.History
                )

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(topBarTitle) },
                            navigationIcon = {
                                val isTopLevel = bottomNavItems.any { it.route == currentRoute }
                                if (!isTopLevel && navController.previousBackStackEntry != null) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    navController.navigate("cart") {
                                        launchSingleTop = true
                                    }
                                }) {
                                    BadgedBox(badge = {
                                        if (cartItemsCount > 0) Badge { Text("$cartItemsCount") }
                                    }) {
                                        Icon(Icons.Default.ShoppingCart, "Cart")
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            bottomNavItems.forEach { item ->
                                val isSelected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    },
                                    icon = {
                                        if (item.route == "favorites") {
                                            BadgedBox(badge = {
                                                if (favoritesCount > 0) Badge { Text("$favoritesCount") }
                                            }) {
                                                Icon(item.icon, contentDescription = item.label)
                                            }
                                        } else {
                                            Icon(item.icon, contentDescription = item.label)
                                        }
                                    },
                                    label = { Text(item.label) }
                                )
                            }
                        }
                    }
                ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = "products",
                            modifier = Modifier.padding(padding).fillMaxSize()
                        ) {
                            composable("products") {
                                ProductsScreen(cartViewModel = cartViewModel, favoritesViewModel = favoritesViewModel, navController = navController)
                            }
                            composable("cart") {
                                CartScreen(cartViewModel = cartViewModel, navController = navController)
                            }
                            composable("checkout") {
                                CheckoutScreen(cartViewModel = cartViewModel, navController = navController)
                            }
                            composable("order_history") {
                                OrderHistoryScreen()
                            }
                            composable("favorites") {
                                FavoritesScreen(navController = navController, cartViewModel = cartViewModel, favoritesViewModel = favoritesViewModel)
                            }
                            composable(
                                "product_detail/{productId}",
                                arguments = listOf(navArgument("productId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val productId = backStackEntry.arguments?.getInt("productId")
                                if (productId != null) {
                                    ProductDetailScreen(
                                        productId = productId,
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}

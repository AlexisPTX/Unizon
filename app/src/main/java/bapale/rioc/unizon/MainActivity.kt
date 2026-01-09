package bapale.rioc.unizon

import bapale.rioc.unizon.screen.CartScreen
import bapale.rioc.unizon.screen.ProductsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bapale.rioc.unizon.viewmodel.CartViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
 
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
 
            val cartViewModel: CartViewModel = viewModel()
            val cartItemsCount by cartViewModel.cartItemCount.collectAsState()
 
            val topBarTitle = when (currentRoute) {
                "products" -> "Products"
                "cart" -> "Cart"
                else -> "Unizon Store"
            }
 
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Spacer(Modifier.height(16.dp))
                        Text("Unizon Store", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                        HorizontalDivider()
 
                        NavigationDrawerItem(
                            label = { Text("Products") },
                            selected = currentRoute == "products",
                            onClick = {
                                navController.navigate("products") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Cart") },
                            selected = currentRoute == "cart",
                            onClick = {
                                navController.navigate("cart") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(topBarTitle) },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, "Menu")
                                }
                            },
                            actions = {
                                IconButton(onClick = { navController.navigate("cart") }) {
                                    BadgedBox(badge = {
                                        if (cartItemsCount > 0) Badge { Text("$cartItemsCount") }
                                    }) {
                                        Icon(Icons.Default.ShoppingCart, "Cart")
                                    }
                                }
                            }
                        )
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "products",
                        modifier = Modifier.padding(padding).fillMaxSize()
                    ) {
                        composable("products") {
                            ProductsScreen(cartViewModel = cartViewModel)
                        }
                        composable("cart") {
                            CartScreen(cartViewModel = cartViewModel)
                        }
                    }
                }
            }
        }
    }
}

package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bapale.rioc.unizon.viewmodel.CartViewModel
import java.text.DecimalFormat

@Composable
fun CheckoutScreen(cartViewModel: CartViewModel, navController: NavController) {
    val totalPrice by cartViewModel.cartTotalPrice.collectAsState()
    val totalItemCount by cartViewModel.cartItemCount.collectAsState()
    val priceFormat = DecimalFormat("#,##0.00")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Order Summary", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total items:", style = MaterialTheme.typography.bodyLarge)
                        Text("$totalItemCount", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total price:", style = MaterialTheme.typography.bodyLarge)
                        Text("${priceFormat.format(totalPrice)} €", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    cartViewModel.placeOrder {
                        // on success
                        navController.navigate("order_history") {
                            // Clear back stack up to products screen
                            popUpTo("products") { inclusive = false }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("CONFIRM PURCHASE", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bapale.rioc.unizon.data.CartItem
import androidx.navigation.NavController
import bapale.rioc.unizon.viewmodel.CartViewModel
import coil.compose.AsyncImage
import java.text.DecimalFormat

@Composable
fun CartScreen(cartViewModel: CartViewModel, navController: NavController) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.cartTotalPrice.collectAsState()
    val totalItemCount by cartViewModel.cartItemCount.collectAsState()
    val priceFormat = DecimalFormat("#,##0.00")

    if (cartItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Your cart is empty.", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    CartListItem(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            cartViewModel.updateQuantity(item, newQuantity)
                        },
                        onRemoveItem = {
                            cartViewModel.removeItemFromCart(item)
                        }
                    )
                }
            }
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val articleText = if (totalItemCount > 1) "products" else "product"
                        Text("Total ($totalItemCount $articleText):", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "${priceFormat.format(totalPrice)} €",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("checkout") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cartItems.isNotEmpty()
                    ) {
                        Text("VALIDATE PURCHASE", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CartListItem(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, maxLines = 2, style = MaterialTheme.typography.titleMedium)
                Text("${item.price} €", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(32.dp),
                    enabled = item.quantity > 1
                ) {
                    Icon(Icons.Default.Remove, "Decrease")
                }
                Text(
                    text = "${item.quantity}",
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, "Increase")
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onRemoveItem, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Remove item")
                }
            }
        }
    }
}
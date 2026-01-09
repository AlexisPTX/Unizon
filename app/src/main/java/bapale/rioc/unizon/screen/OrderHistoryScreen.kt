package bapale.rioc.unizon.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bapale.rioc.unizon.data.OrderWithItems
import bapale.rioc.unizon.viewmodel.OrderHistoryViewModel
import coil.compose.AsyncImage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen(orderHistoryViewModel: OrderHistoryViewModel = viewModel()) {
    val orders by orderHistoryViewModel.orders.collectAsState()

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No past orders found.", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders) { orderWithItems ->
                OrderHistoryItem(orderWithItems)
            }
        }
    }
}

@Composable
fun OrderHistoryItem(orderWithItems: OrderWithItems) {
    var expanded by remember { mutableStateOf(false) }
    val priceFormat = DecimalFormat("#,##0.00")
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order from ${dateFormat.format(Date(orderWithItems.order.timestamp))}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${orderWithItems.items.sumOf { it.quantity }} products",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${priceFormat.format(orderWithItems.order.totalPrice)} €",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    orderWithItems.items.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.title,
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, maxLines = 2, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Qty: ${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${priceFormat.format(item.price * item.quantity)} €",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
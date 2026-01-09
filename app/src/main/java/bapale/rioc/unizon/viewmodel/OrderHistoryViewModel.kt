package bapale.rioc.unizon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bapale.rioc.unizon.data.AppDatabase
import bapale.rioc.unizon.data.OrderWithItems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val orderDao = AppDatabase.getDatabase(application).orderDao()

    val orders: StateFlow<List<OrderWithItems>> = orderDao.getOrdersWithItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
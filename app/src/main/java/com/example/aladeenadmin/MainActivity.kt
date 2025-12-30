package com.example.aladeenadmin

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aladeenadmin.ui.login.LoginScreen
import com.example.aladeenadmin.ui.orders.OrderDetailScreen
import com.example.aladeenadmin.ui.orders.OrdersScreen
import com.example.aladeenadmin.utils.TokenManager
import com.example.aladeenadmin.viewmodel.LoginViewModel
import com.example.aladeenadmin.viewmodel.OrderDetailViewModel
import com.example.aladeenadmin.viewmodel.OrdersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminApp()
                }
            }
        }
    }
}

@Composable
fun AdminApp() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.getToken.collectAsState(initial = null)

    var selectedOrderId by remember { mutableStateOf<Int?>(null) }

    if (token == null) {
        val loginViewModel: LoginViewModel = viewModel()
        LoginScreen(viewModel = loginViewModel)
    } else {
        if (selectedOrderId == null) {
            val ordersViewModel: OrdersViewModel = viewModel()
            OrdersScreen(
                viewModel = ordersViewModel,
                onOrderClick = { id -> selectedOrderId = id }
            )
        } else {
            val orderDetailViewModel: OrderDetailViewModel = viewModel(
                factory = OrderDetailViewModelFactory(
                    context.applicationContext as Application,
                    selectedOrderId!!
                )
            )
            OrderDetailScreen(
                viewModel = orderDetailViewModel,
                onBack = { selectedOrderId = null }
            )
        }
    }
}

class OrderDetailViewModelFactory(
    private val application: Application,
    private val orderId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderDetailViewModel(application, orderId) as T
    }
}

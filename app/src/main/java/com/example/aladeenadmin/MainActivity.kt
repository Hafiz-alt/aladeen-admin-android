package com.example.aladeenadmin

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aladeenadmin.ui.login.LoginScreen
import com.example.aladeenadmin.ui.menu.MenuScreen
import com.example.aladeenadmin.ui.orders.OrderDetailScreen
import com.example.aladeenadmin.ui.orders.OrdersScreen
import com.example.aladeenadmin.ui.theme.AladeenAdminTheme
import com.example.aladeenadmin.ui.upi.UPIScreen
import com.example.aladeenadmin.utils.TokenManager
import com.example.aladeenadmin.viewmodel.LoginViewModel
import com.example.aladeenadmin.viewmodel.MenuViewModel
import com.example.aladeenadmin.viewmodel.OrderDetailViewModel
import com.example.aladeenadmin.viewmodel.OrdersViewModel
import com.example.aladeenadmin.viewmodel.UPIViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val orderIdFromIntent = intent.getStringExtra("order_id")?.toIntOrNull()

        setContent {
            AladeenAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminApp(initialOrderId = orderIdFromIntent)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val orderIdFromIntent = intent.getStringExtra("order_id")?.toIntOrNull()
        if (orderIdFromIntent != null) {
            intent.putExtra("order_id", orderIdFromIntent.toString())
            recreate() 
        }
    }
}

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Orders : Screen("orders", Icons.Default.List, "Orders")
    object Menu : Screen("menu", Icons.Default.Menu, "Menu")
    object UPI : Screen("upi", Icons.Default.Payments, "UPI")
}

@Composable
fun AdminApp(initialOrderId: Int? = null) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.getToken.collectAsState(initial = null)

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Orders) }
    var selectedOrderId by remember { mutableStateOf<Int?>(initialOrderId) }

    if (token == null) {
        val loginViewModel: LoginViewModel = viewModel()
        LoginScreen(viewModel = loginViewModel)
    } else {
        Scaffold(
            bottomBar = {
                if (selectedOrderId == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        val items = listOf(Screen.Orders, Screen.Menu, Screen.UPI)
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.label) },
                                label = { Text(screen.label) },
                                selected = currentScreen == screen,
                                onClick = { currentScreen = screen },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    Screen.Orders -> {
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
                    Screen.Menu -> {
                        val menuViewModel: MenuViewModel = viewModel()
                        MenuScreen(viewModel = menuViewModel)
                    }
                    Screen.UPI -> {
                        val upiViewModel: UPIViewModel = viewModel()
                        UPIScreen(viewModel = upiViewModel)
                    }
                }
            }
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

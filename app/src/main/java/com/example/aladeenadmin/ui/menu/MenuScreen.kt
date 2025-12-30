package com.example.aladeenadmin.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.DeviceUnregistrationRequest
import com.example.aladeenadmin.data.model.Snack
import com.example.aladeenadmin.utils.TokenManager
import com.example.aladeenadmin.viewmodel.MenuViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(viewModel: MenuViewModel) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val scope = rememberCoroutineScope()
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Management", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            try {
                                val token = FirebaseMessaging.getInstance().token.await()
                                RetrofitClient.getApiService(context).unregisterDevice(
                                    DeviceUnregistrationRequest(deviceToken = token)
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                tokenManager.clearToken()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.menuItems) { snack ->
                        SnackItemCard(snack, onEditClick = { selectedSnack = snack })
                    }
                }
            }
        }
    }

    selectedSnack?.let { snack ->
        EditSnackDialog(
            snack = snack,
            onDismiss = { selectedSnack = null },
            onConfirm = { price, stock, isAvailable ->
                viewModel.updateSnack(snack.id, price, stock, isAvailable)
                selectedSnack = null
            }
        )
    }
}

@Composable
fun SnackItemCard(snack: Snack, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = snack.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "Price: ₹${snack.price}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Stock: ${snack.stock}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (snack.isAvailable) "Available" else "Not Available",
                    color = if (snack.isAvailable) Color(0xFF2E7D32) else Color.Red,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Button(onClick = onEditClick) {
                Text("Edit")
            }
        }
    }
}

@Composable
fun EditSnackDialog(
    snack: Snack,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Boolean) -> Unit
) {
    var price by remember { mutableStateOf(snack.price.toString()) }
    var stock by remember { mutableStateOf(snack.stock.toString()) }
    var isAvailable by remember { mutableStateOf(snack.isAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${snack.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    Text("Is Available")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(price.toIntOrNull() ?: snack.price, stock.toIntOrNull() ?: snack.stock, isAvailable)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

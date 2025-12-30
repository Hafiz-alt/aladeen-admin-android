package com.example.aladeenadmin.ui.upi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.DeviceUnregistrationRequest
import com.example.aladeenadmin.data.model.UPIAccount
import com.example.aladeenadmin.utils.TokenManager
import com.example.aladeenadmin.viewmodel.UPIViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UPIScreen(viewModel: UPIViewModel) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UPI Management", color = MaterialTheme.colorScheme.onPrimary) },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add UPI")
            }
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
                    items(viewModel.upiAccounts) { upi ->
                        UPIItemCard(
                            upi = upi,
                            onActivate = { id -> viewModel.activateUPIAccount(id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddUPIDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { upiId ->
                viewModel.createUPIAccount(upiId)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun UPIItemCard(upi: UPIAccount, onActivate: (Int) -> Unit) {
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
                Text(text = upi.upiId, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (upi.isActive) "Active" else "Inactive",
                    color = if (upi.isActive) Color(0xFF2E7D32) else Color.Gray,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (!upi.isActive) {
                Button(onClick = { upi.id?.let { onActivate(it) } }) {
                    Text("Activate")
                }
            }
        }
    }
}

@Composable
fun AddUPIDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var upiId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add UPI Account") },
        text = {
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("example@upi") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (upiId.isNotBlank()) onConfirm(upiId) },
                enabled = upiId.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

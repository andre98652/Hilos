package com.example.hiloscomposedemo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Esta es la pantalla principal que verá el usuario.
 *
 * Recibe un MainViewModel como parámetro.
 * La Activity será la que cree el ViewModel y se lo pase.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    // Observamos el StateFlow<UiState> del ViewModel como un state de Compose.
    val state: UiState by viewModel.uiState.collectAsState()

    // Contexto actual de Android (necesario para WorkManager).
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensaje principal (qué está pasando)
            Text(text = state.message)

            // Si tenemos nombre de hilo, lo mostramos
            if (state.threadName.isNotBlank()) {
                Text(
                    text = state.threadName,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón que usa coroutines + viewModelScope + Dispatchers + withContext
            Button(
                onClick = {
                    viewModel.loadDataWithCoroutines()
                }
            ) {
                Text("Cargar datos (Coroutine)")
            }

            // Botón que programa tarea en segundo plano con WorkManager
            Button(
                onClick = {
                    viewModel.startBackgroundSync(context)
                }
            ) {
                Text("Sincronizar en segundo plano (WorkManager)")
            }
        }
    }
}

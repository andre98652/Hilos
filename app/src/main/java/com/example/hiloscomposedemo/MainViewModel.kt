package com.example.hiloscomposedemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

// ViewModel = clase que maneja la lógica y el estado de la pantalla.
class MainViewModel : ViewModel() {

    // Estado interno que podemos modificar dentro del ViewModel.
    // MutableStateFlow = flujo de datos que puede cambiar.
    private val _uiState = MutableStateFlow(UiState())

    // Estado que la UI va a observar (solo lectura).
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Esta función simula una carga de datos usando coroutines.
     *
     * Conceptos que se ven aquí:
     * - viewModelScope: "caja" donde metemos tareas del ViewModel.
     * - Main Thread: al inicio de la coroutine.
     * - Worker Thread: dentro de withContext(Dispatchers.IO).
     * - withContext(): cambia temporalmente de hilo.
     */
    fun loadDataWithCoroutines() {
        // Lanzamos una coroutine en el scope del ViewModel.
        // Por defecto, esto corre en el Main Thread (UI Thread).
        viewModelScope.launch {

            // Guardamos el nombre del hilo actual (debería ser "main").
            val mainThreadName = Thread.currentThread().name

            // Actualizamos el estado: la UI mostrará que estamos cargando.
            _uiState.value = UiState(
                message = "Cargando datos en segundo plano...",
                threadName = "Actualizado en hilo: $mainThreadName"
            )

            // Ahora cambiamos a un Worker Thread usando Dispatchers.IO.
            val resultFromWorker = withContext(Dispatchers.IO) {

                // Aquí ya NO estamos en el hilo principal, sino en un hilo de trabajo.
                val workerThreadName = Thread.currentThread().name

                // Simulamos una tarea pesada (llamada a servidor, base de datos, etc.).
                delay(3000) // 3 segundos de espera.

                // Devolvemos un texto con el nombre del hilo de trabajo.
                "Datos cargados desde Worker Thread: $workerThreadName"
            }

            // Cuando termina withContext, volvemos al hilo original (Main Thread).
            val finalMainThreadName = Thread.currentThread().name

            // Actualizamos el estado otra vez, ahora con el resultado.
            _uiState.value = UiState(
                message = resultFromWorker,
                threadName = "UI actualizada en hilo: $finalMainThreadName"
            )
        }
    }

    /**
     * Esta función programa una tarea en segundo plano usando WorkManager.
     * El Worker (SyncWorker) se ejecutará en Worker Threads y puede seguir
     * corriendo aunque la app no esté abierta.
     */
    fun startBackgroundSync(context: Context) {
        // Creamos una petición de trabajo de una sola vez
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()

        // Encolamos (programamos) la tarea en WorkManager
        WorkManager.getInstance(context).enqueue(request)

        // Obtenemos el hilo actual (normalmente será "main")
        val currentThreadName = Thread.currentThread().name

        // Actualizamos el estado para avisar al usuario
        _uiState.value = _uiState.value.copy(
            message = "Sincronización programada con WorkManager.\nRevisa Logcat para ver el hilo.",
            threadName = "startBackgroundSync ejecutado en hilo: $currentThreadName"
        )
    }


}

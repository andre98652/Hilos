package com.example.hiloscomposedemo

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

/**
 * Este Worker lo va a ejecutar WorkManager.
 * Representa una tarea de "sincronización" en segundo plano.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // Esta función se ejecuta cuando WorkManager corre este trabajo.
    override suspend fun doWork(): Result {
        return try {
            // Cambiamos explícitamente a un Dispatcher.IO (Worker Thread)
            val result = withContext(Dispatchers.IO) {
                val workerThreadName = Thread.currentThread().name
                Log.d("SyncWorker", "Sincronizando en hilo: $workerThreadName")

                // Simulamos una tarea pesada de 5 segundos
                delay(5000)

                "Sincronización completada en hilo: $workerThreadName"
            }

            Log.d("SyncWorker", result)

            // Avisamos a WorkManager que todo salió bien
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error en sincronización", e)
            // Avisamos que la tarea falló
            Result.failure()
        }
    }
}

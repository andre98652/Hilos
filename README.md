# 🧵 Demostración de Hilos en Android con Jetpack Compose

Este proyecto fue creado para mostrar cómo Android puede ejecutar tareas en distintos hilos sin bloquear la interfaz.
Se utiliza **Jetpack Compose** para la UI, un **ViewModel** para manejar la lógica con coroutines, y **WorkManager** para ejecutar tareas de fondo garantizadas.

---

## 👥 Integrantes del Equipo

- Delgado Allpan, Andree David
- Hilacondo Begazo, Andre Jimmy
- Escobedo Ocaña, Jorge Luis
- Roque Quispe, William Isaias
- Gordillo Mendoza, Jose Alonzo  

---

## 🚀 ¿Qué se hizo en el proyecto?

### ✅ 1. Crear una UI fluida con Jetpack Compose
- Se diseñó una pantalla simple con un texto de estado y dos botones.
- Toda la interfaz se ejecuta en el **hilo principal (Main Thread / UI Thread)** para responder rápido al usuario.

### ✅ 2. Crear un ViewModel para manejar las tareas
- Se creó `MainViewModel.kt` como contenedor de la lógica.
- Dentro se usa `viewModelScope` para lanzar tareas (coroutines) mientras la pantalla esté activa.
- Se implementó la función `loadDataWithCoroutines()` que:
  1. Inicia en el **Main Thread**.
  2. Cambia temporalmente a `Dispatchers.IO` (Worker Thread) con `withContext()` para simular carga pesada de 3 segundos.
  3. Regresa al Main Thread para actualizar el texto de la UI mostrando el resultado.

### ✅ 3. Crear un Worker para WorkManager
- Se creó `SyncWorker.kt` que implemente `CoroutineWorker()`.
- WorkManager ejecuta esta tarea durante 5 segundos en un Worker Thread.
- Se programó desde el ViewModel usando `OneTimeWorkRequest()` y se encoló a WorkManager.
- En Logcat se confirma el nombre del hilo usado y el resultado exitoso (`SUCCESS`).

### ✅ 4. Usar lifecycleScope en MainActivity
- Se añadió en `MainActivity.kt` dos tareas demo para mostrar que lifecycleScope también puede:
  - Ejecutar tareas en **Main Thread**.
  - Ejecutar tareas en **Worker Thread con Dispatchers.IO**.

---

## 🧠 Resumen conceptual de ejecución

| Componente | Hilo donde corre | Propósito |
|---|---|---|
| Jetpack Compose UI | `main` (Main Thread) | Dibujar pantalla y responder al usuario |
| ViewModel coroutine (inicio/fin) | `main` | Gestionar interacción con UI |
| `withContext(Dispatchers.IO)` | Pool de hilos tipo `worker-X` | Simular o ejecutar tareas pesadas I/O |
| WorkManager (SyncWorker) | Pool `worker-X` | Tareas garantizadas en segundo plano |
| `lifecycleScope.launch {}` | `main` | Tareas ligadas a la vida de la Activity |
| `lifecycleScope.launch(IO) {}` | `worker-X` | Misma caja pero en hilo secundario |

---

## 📌 ¿Cómo funcionan los hilos en este ejemplo?

Android evita que la UI se congele siguiendo esta estrategia:

1. La Activity crea la pantalla Compose → se ejecuta en `main`.
2. El usuario toca un botón → ese evento se recibe en `main`.
3. El ViewModel **lanza una coroutine**, que empieza en `main`.
4. `withContext(IO)` manda el trabajo pesado a otro hilo (**Worker Thread**).
5. Cuando termina, vuelve al Main Thread para actualizar el estado observado por Compose.
6. WorkManager ejecuta el Worker en otro hilo separado y “garantizado”, independientemente de la UI.

💬 Esto se puede visualizar en Logcat como:
- `DefaultDispatcher-worker-3` → Hilo de fondo donde se ejecutó el Worker.
- `Result.SUCCESS` → WorkManager confirma que la tarea terminó correctamente.

---

## 🏁 Conclusiones

### 🔍 Buenas prácticas demostradas
- ✅ Separar la UI del trabajo pesado.
- ✅ No crear hilos manualmente, sino dejar que `Dispatcher` y `WorkManager` los gestionen.
- ✅ Usar `viewModelScope` y `lifecycleScope` para evitar que las tareas sigan cuando ya no se necesitan.
- ✅ Actualizar la UI solo cuando estás de vuelta en `main`.
- ✅ WorkManager es ideal para tareas que deben continuar aunque el usuario cierre la app.

### 🎓 Lo que se aprende
- **Scope** = una caja que agrupa tareas y decide cuándo cancelarlas.
- **Dispatcher** = el que decide qué hilo usar.
- `withContext()` = mover una parte del trabajo a otro hilo temporalmente.
- **Main Thread** = solo para UI y eventos rápidos.
- **Worker Threads** = para tareas largas, red, base de datos o procesos pesados.
- **WorkManager** = ejecutar tareas garantizadas en background.

### ⚡ Resultado final
Una app que ejecuta tareas pesadas sin congelar la interfaz, mostrando claramente los hilos involucrados en el flujo.

---
> 🏆 Con esto se cumple la tarea: demostrar el manejo de hilos y background execution en Android moderno con Compose + ViewModel + Coroutines + WorkManager.

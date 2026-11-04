# Contrato de integracion (version implementada)

Clases que extienden `Thread`: `Usuario`, `Gestor`, `MaquinaVirtual`.

`Gestor` usa el constructor `new MaquinaVirtual(Solicitud, ControladorProcesadores, ControladorMemoria, ControladorAlmacenamiento, ZonaEspera)`.

`Solicitud`: `getId()`, `getProcesadores()`, `getMemoriaGiB()`, `getAlmacenamientoGiB()`.

`BuzonEntrada`: `depositar(Solicitud)` (espera pasiva), `intentarRetirar()` (no bloqueante), `terminado()`, `usuarioTermino()`.

`ZonaEspera`: `esperarSiSaturada()` (gestor), `registrarPendiente(mv)`, `completarPendiente(mv)`, `esperarCambio()` y `notificarLiberacion()`.

Los tres controladores: `reservar(cantidad, mv)`, `liberar(mv)`, `libres()`, `estaTodoLibre()`; memoria y almacenamiento traducen GiB a paginas/bloques.

`Main`: lanza y espera los threads y valida contador de MVs, buzón, zona y recursos. Los gestores esperan a las MVs que crearon.

**Regla clave:** las MVs mantienen el monitor de `ZonaEspera` entre el intento de reserva y su `wait()`, evitando notificaciones perdidas. Los controladores nunca invocan a ZonaEspera mientras conservan su propio monitor. Se evita la retencion circular de monitores.

**Interpretacion de tam2:** umbral de MVs pendientes que detiene nuevas admisiones por parte de los gestores hasta el vaciado de la zona. El diagrama del enunciado presenta una zona ilimitada con umbral; en la lista de parametros tam2 aparece como capacidad. Documentar esta interpretacion.

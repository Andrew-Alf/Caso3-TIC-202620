# Contrato entre Persona 1 y Persona 2

Constructor que usa el Gestor:
`new MaquinaVirtual(Solicitud, ControladorProcesadores, ControladorMemoria, ControladorAlmacenamiento, ZonaEspera)`

Solicitud (inmutable): `getId()`, `getProcesadores()`, `getMemoriaGiB()`, `getAlmacenamientoGiB()`.

ZonaEspera (el Gestor solo usa):
- `esperarSiSaturada()`: si pendientes >= umbral, bloquea hasta que pendientes == 0 (check y wait atomicos).
- `pendientes()`: MVs dormidas ahora mismo.
- Persona 2 debe hacer `notifyAll()` cuando pendientes llegue a 0.

Controladores (cada uno): `boolean reservar(cantidad, mv)`, `void liberar(mv)`, `int libres()`, `boolean estaTodoLibre()`.

Main (validacion final) usa `estaTodoLibre()` de los tres controladores y `zona.pendientes()`.

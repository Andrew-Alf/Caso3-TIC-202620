# Resultados de validacion del codigo

Las siguientes pruebas se ejecutaron en Java 21, compilando todos los `src/*.java`. No hay dependencias externas.

| Prueba | Resultado observado |
|---|---|
| Compilacion con `javac -Xlint:all` | Sin errores ni advertencias |
| `PruebaControladores` | CORRECTA: reservas atomicas, asignacion exclusiva y liberacion de CPU, paginas y bloques |
| `PruebaBuzon` | CORRECTA: 4 productores, 4 consumidores, buzon de capacidad 2, 200 solicitudes unicas, sin perdidas |
| `Main config.txt` en modo prueba | 12 esperadas / 12 creadas / 12 completadas; buzon y zona vacios; todos los recursos libres |
| `Main pruebas/config_contencion.txt` en modo prueba | 30 / 30 / 30; sin perdidas ni bloqueos; todos los recursos libres |
| `Main pruebas/config_umbral1.txt` en modo prueba | 30 / 30 / 30; umbral 1; todos los recursos libres |
| `Main pruebas/config_sin_contencion.txt` en modo prueba | 6 / 6 / 6; todos los recursos libres |
| Repeticion de contencion (12 ejecuciones adicionales) | 12 ejecuciones CORRECTAS; 30 solicitudes cada una |

**Nota importante:** los escenarios de prueba usan `-Dcaso3.prueba.duracionMs=60` o `100` para hacerlos rapidos. La ejecucion estandar, sin esa propiedad, usa un intervalo aleatorio de 4000 a 40000 milisegundos por maquina virtual. Las pruebas de modo rapido no constituyen una medicion de tiempos de produccion.

**Evidencia de espera:** en la prueba basica se observaron mensajes `[MV ...] pendiente; espera recursos` y `[ZonaEspera] Gestor-... espera vaciado`, seguidos por la reanudacion y terminacion correcta.

## Comandos sugeridos en Windows PowerShell

```powershell
javac -d out src\*.java
java -cp out PruebaInicial
java -cp out PruebaControladores
java -cp out PruebaBuzon
java -Dcaso3.prueba.duracionMs=100 -cp out Main config.txt
java -Dcaso3.prueba.duracionMs=60 -cp out Main pruebas/config_umbral1.txt
java -Dcaso3.prueba.duracionMs=60 -cp out Main pruebas/config_contencion.txt
java -cp out Main config.txt
```

La ultima instruccion usa los tiempos reales del caso y puede tardar varios minutos. Todas las salidas se pueden redirigir a archivos para conservar evidencia.

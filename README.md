# Caso 3 - Tecnologia e Infraestructura de Computo

Simulador concurrente en Java, con productores (`Usuario`), consumidores (`Gestor`), maquinas virtuales y controladores de procesadores, memoria y almacenamiento.

## Requisitos

Java JDK 8 o superior. No requiere bibliotecas externas.

## Ejecutar en PowerShell (desde la carpeta raiz)

```powershell
javac -d out src\*.java
java -cp out Main config.txt
```

La ejecucion **normal** hace que cada MV use sus recursos durante un tiempo aleatorio entre **4000 y 40000 ms**. El sistema podria tardar varios minutos en terminar dependiendo del numero de solicitudes y recursos.

## Prueba rapida de concurrencia

```powershell
java -Dcaso3.prueba.duracionMs=100 -cp out Main config.txt
```

La propiedad **opcional y exclusiva de pruebas** acorta el tiempo de ejecucion de cada MV. Si no se proporciona, se respeta el enunciado (4 a 40 segundos).

## Validacion

Se espera ver `VALIDACION: CORRECTA` al final, con igual cantidad de solicitudes esperadas, MVs creadas y MVs completadas. Los procesadores, paginas y bloques deben quedar totalmente libres. Revisar durante la ejecucion los registros `pendiente; espera recursos` y `reservo CPU/RAM/disco`.

Para provocar mas contencion: reducir procesadores y memorias, disminuir `tam2` (por ejemplo 1) o aumentar `nu`/`base`. Los valores deben continuar siendo positivos.

## Sincronizacion

- Buzon `synchronized`; usuario `wait()` cuando lleno, gestor `yield()` cuando vacio.
- Tres controladores independientes guardan un array de propietarios por unidad fisica (CPU, pagina, bloque), y reservan atomica o totalmente por controlador.
- MVs reservan CPU->memoria->disco; si falla la reserva, sueltan TODO lo ya asignado.
- MVs esperan con `wait()` sobre el monitor de la zona, sin perdidas de notificaciones; las liberaciones llaman a `notifyAll()`.
- Gestor detiene admision si el numero de pendientes alcanza `tam2`, y no reinicia hasta que la zona se vacie.
- Todos los threads terminan mediante `join()` y se comprueba el estado final.

## Consideraciones de interpretacion

El enunciado denomina `tam2` capacidad de la zona, mientras su diagrama muestra zona ilimitada con umbral. Se toma `tam2` como **umbral**; el numero de MVs pendientes puede aumentar en una carrera de admisiones simultaneas de gestores. La espera de los gestores impide continuar admitiendo cuando ven el umbral alcanzado.

Memoria y disco se convierten de GiB a paginas/bloques enteros redondeando hacia arriba si no son multiplos exactos, para nunca asignar menos de lo solicitado. El generador redondea RAM hacia abajo a potencia de 2 y disco al multiplo par inferior, conservando 1 GiB como minimo.

## Archivos

`src/*.java` contiene el codigo fuente del programa. `config.txt` contiene parametros. `CONTRATO_API.md` documenta metodos de integracion.

## Pruebas automatizadas

Ejecutar `./probar.ps1` en PowerShell desde la raiz del proyecto, o utilizar los comandos de `RESULTADOS_PRUEBAS.md`.

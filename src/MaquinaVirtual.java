/**
 * PERSONA 2: una MV es un thread que reserva CPU, memoria y disco, "trabaja" y libera todo.
 * Si no logra reservar, se duerme en la ZonaEspera.
 *
 * Constructor acordado (el que usa el Gestor):
 *   MaquinaVirtual(Solicitud, ControladorProcesadores, ControladorMemoria,
 *                  ControladorAlmacenamiento, ZonaEspera)
 */
public class MaquinaVirtual extends Thread {

    private final Solicitud solicitud;
    private final ControladorProcesadores ctrlProcesadores;
    private final ControladorMemoria ctrlMemoria;
    private final ControladorAlmacenamiento ctrlAlmacenamiento;
    private final ZonaEspera zona;

    public MaquinaVirtual(Solicitud solicitud,
                          ControladorProcesadores ctrlProcesadores,
                          ControladorMemoria ctrlMemoria,
                          ControladorAlmacenamiento ctrlAlmacenamiento,
                          ZonaEspera zona) {
        super("MV-" + solicitud.getId());
        this.solicitud = solicitud;
        this.ctrlProcesadores = ctrlProcesadores;
        this.ctrlMemoria = ctrlMemoria;
        this.ctrlAlmacenamiento = ctrlAlmacenamiento;
        this.zona = zona;
    }

    public Solicitud getSolicitud() { return solicitud; }

    @Override
    public void run() {
        // TODO(Persona 2): reservar recursos (sin bloqueos), dormir en la zona si falla,
        //                  simular ejecucion (4 a 40 s en el enunciado), liberar y despertar a otras.
        // Placeholder para poder probar el flujo de Persona 1:
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[MV " + solicitud.getId() + "] (placeholder) termino");
    }
}

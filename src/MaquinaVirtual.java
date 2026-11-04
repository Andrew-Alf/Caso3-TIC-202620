import java.util.Random;

/** Thread que reserva CPU, memoria y disco, ejecuta, libera y despierta a las MVs. */
public class MaquinaVirtual extends Thread {
    private final Solicitud solicitud;
    private final ControladorProcesadores ctrlProcesadores;
    private final ControladorMemoria ctrlMemoria;
    private final ControladorAlmacenamiento ctrlAlmacenamiento;
    private final ZonaEspera zona;
    private final long duracionMs;
    private boolean ejecucionCompleta = false;

    public MaquinaVirtual(Solicitud solicitud,
            ControladorProcesadores cpu, ControladorMemoria ram,
            ControladorAlmacenamiento disco, ZonaEspera zona) {
        super("MV-" + solicitud.getId());
        this.solicitud = solicitud;
        this.ctrlProcesadores = cpu;
        this.ctrlMemoria = ram;
        this.ctrlAlmacenamiento = disco;
        this.zona = zona;
        // Modo de prueba OPCIONAL: solo para verificar la sincronizacion rapidamente.
        // En ejecucion normal la duracion SIEMPRE esta entre 4000 y 40000 ms.
        String prueba = System.getProperty("caso3.prueba.duracionMs");
        this.duracionMs = prueba == null
                ? 4000L + new Random().nextInt(36001)
                : Long.parseLong(prueba);
        if (duracionMs <= 0) throw new IllegalArgumentException("Duracion invalida");
    }

    public Solicitud getSolicitud() { return solicitud; }
    /** Leer despues de join() para obtener el resultado definitivo. */
    public boolean ejecucionCompleta() { return ejecucionCompleta; }

    /**
     * Se llama con el monitor zona tomado. No espera sosteniendo ningun monitor
     * de controlador. Si falla, devuelve todos los recursos parciales antes de dormir.
     */
    private boolean intentarReservarRecursos() {
        if (!ctrlProcesadores.reservar(solicitud.getProcesadores(), this)) return false;
        if (!ctrlMemoria.reservar(solicitud.getMemoriaGiB(), this)) {
            ctrlProcesadores.liberar(this);
            return false;
        }
        if (!ctrlAlmacenamiento.reservar(solicitud.getAlmacenamientoGiB(), this)) {
            ctrlMemoria.liberar(this);
            ctrlProcesadores.liberar(this);
            return false;
        }
        return true;
    }

    private void liberarRecursos() {
        // Liberar los tres sin mantener el monitor de ninguno al entrar a la zona.
        ctrlAlmacenamiento.liberar(this);
        ctrlMemoria.liberar(this);
        ctrlProcesadores.liberar(this);
        zona.notificarLiberacion();
    }

    @Override public void run() {
        boolean configurada = false;
        try {
            while (!configurada) {
                // Tomar el monitor de la zona tanto para reservar como para entrar a wait
                // evita perdida de notificaciones entre comprobar y esperar.
                synchronized (zona) {
                    configurada = intentarReservarRecursos();
                    if (configurada) {
                        zona.completarPendiente(this);
                        System.out.println("[MV " + solicitud.getId() + "] reservo CPU/RAM/disco");
                    } else {
                        if (zona.registrarPendiente(this)) {
                            System.out.println("[MV " + solicitud.getId() + "] pendiente; espera recursos");
                        }
                        zona.esperarCambio(); // wait libera el monitor de zona.
                    }
                }
            }
            System.out.println("[MV " + solicitud.getId() + "] ejecutando " + duracionMs + " ms");
            Thread.sleep(duracionMs);
            ejecucionCompleta = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[MV " + solicitud.getId() + "] interrumpida");
        } finally {
            // Si fue interrumpida antes de configurarse, tampoco deja un pendiente fantasma.
            zona.completarPendiente(this);
            liberarRecursos();
            System.out.println("[MV " + solicitud.getId() + "] libero recursos y termino");
        }
    }
}

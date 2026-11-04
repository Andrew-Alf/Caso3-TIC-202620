import java.util.HashSet;
import java.util.Set;

/**
 * Monitor compartido para las MVs pendientes y los gestores.
 * Se mantiene registrada una MV hasta conseguir los tres recursos,
 * NO solamente hasta que despierta de wait().
 *
 * El parametro tam2 se interpreta como el umbral de MVs pendientes.
 */
public class ZonaEspera {
    private final int umbral;
    private final Set<MaquinaVirtual> pendientes = new HashSet<>();

    public ZonaEspera(int umbral) {
        if (umbral <= 0) throw new IllegalArgumentException("Umbral no positivo");
        this.umbral = umbral;
    }

    public int getUmbral() { return umbral; }
    public synchronized int pendientes() { return pendientes.size(); }

    /** Llamado por un gestor antes de tomar una nueva solicitud. */
    public synchronized void esperarSiSaturada() throws InterruptedException {
        if (pendientes.size() >= umbral) {
            // Espera total: permanece esperando hasta que no queden MVs pendientes.
            System.out.println("[ZonaEspera] " + Thread.currentThread().getName()
                    + " espera vaciado (pendientes=" + pendientes.size() + ")");
            while (!pendientes.isEmpty()) wait();
            System.out.println("[ZonaEspera] " + Thread.currentThread().getName()
                    + " reanuda admision (pendientes=0)");
        }
    }

    /** Debe llamarse con el mismo monitor que protege el intento de reserva. */
    public synchronized boolean registrarPendiente(MaquinaVirtual mv) {
        return pendientes.add(mv);
    }

    /** Una MV deja de estar pendiente cuando consigue todos los recursos o termina por interrupcion. */
    public synchronized void completarPendiente(MaquinaVirtual mv) {
        if (pendientes.remove(mv) && pendientes.isEmpty()) notifyAll();
    }

    /**
     * Requiere que el llamador mantenga el monitor de la zona durante el intento
     * de reserva y la entrada a wait(), evitando que se pierda una notificacion.
     */
    public synchronized void esperarCambio() throws InterruptedException {
        wait();
    }

    /** Se llama despues de liberar recursos; despierta a todas las MVs interesadas. */
    public synchronized void notificarLiberacion() {
        notifyAll();
    }
}

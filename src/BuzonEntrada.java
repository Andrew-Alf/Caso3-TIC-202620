import java.util.LinkedList;
import java.util.Queue;

/**
 * Buzon de entrada: cola de capacidad limitada compartida entre usuarios (productores)
 * y gestores (consumidores).
 *
 * - Usuarios: ESPERA PASIVA (wait/notifyAll) cuando el buzon esta lleno.
 * - Gestores: ESPERA SEMI-ACTIVA (Thread.yield) cuando el buzon esta vacio.
 * - Tambien lleva la cuenta de usuarios activos para que los gestores sepan cuando terminar.
 */
public class BuzonEntrada {

    private final Queue<Solicitud> cola = new LinkedList<>();
    private final int capacidad;
    private int usuariosActivos;

    public BuzonEntrada(int capacidad, int numeroUsuarios) {
        this.capacidad = capacidad;
        this.usuariosActivos = numeroUsuarios;
    }

    /** Llamado por los usuarios. Espera pasiva mientras el buzon este lleno. */
    public synchronized void depositar(Solicitud s) throws InterruptedException {
        while (cola.size() == capacidad) {
            wait();
        }
        cola.add(s);
        // No hace falta notificar a los gestores: ellos hacen espera semi-activa.
    }

    /**
     * Llamado por los gestores. Espera semi-activa: si no hay solicitudes pero aun hay
     * usuarios activos, suelta el monitor, cede la CPU con yield y vuelve a intentar.
     *
     * @return la siguiente solicitud, o null si ya no habra mas (buzon vacio y 0 usuarios activos)
     */
    public Solicitud retirar() {
        while (true) {
            synchronized (this) {
                if (!cola.isEmpty()) {
                    Solicitud s = cola.poll();
                    notifyAll(); // despierta a los usuarios que esperan por espacio
                    return s;
                }
                if (usuariosActivos == 0) {
                    return null; // no hay solicitudes y nadie mas va a producir
                }
            }
            Thread.yield(); // fuera del synchronized: no retiene el monitor mientras cede la CPU
        }
    }

    /** Un usuario reporta que termino de producir todas sus solicitudes. */
    public synchronized void usuarioTermino() {
        usuariosActivos--;
    }

    public synchronized int getUsuariosActivos() { return usuariosActivos; }

    public synchronized boolean estaVacio() { return cola.isEmpty(); }
}

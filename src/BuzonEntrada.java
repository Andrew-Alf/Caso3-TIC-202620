import java.util.LinkedList;
import java.util.Queue;

/** Buzon productor-consumidor con capacidad limitada. */
public class BuzonEntrada {
    private final Queue<Solicitud> cola = new LinkedList<>();
    private final int capacidad;
    private int usuariosActivos;

    public BuzonEntrada(int capacidad, int numeroUsuarios) {
        if (capacidad <= 0 || numeroUsuarios <= 0)
            throw new IllegalArgumentException("Capacidad y usuarios deben ser positivos");
        this.capacidad = capacidad;
        this.usuariosActivos = numeroUsuarios;
    }

    /** Los usuarios esperan pasivamente al llenarse el buzon. */
    public synchronized void depositar(Solicitud solicitud) throws InterruptedException {
        if (solicitud == null) throw new IllegalArgumentException("Solicitud nula");
        while (cola.size() >= capacidad) wait();
        cola.add(solicitud);
        notifyAll();
    }

    /** No bloquea: el Gestor implementa por fuera su espera semi-activa. */
    public synchronized Solicitud intentarRetirar() {
        Solicitud solicitud = cola.poll();
        if (solicitud != null) notifyAll(); // Hay espacio para productores.
        return solicitud;
    }

    /** Compatibilidad con el contrato preliminar: espera semi-activa al retirar. */
    public Solicitud retirar() {
        while (true) {
            Solicitud solicitud = intentarRetirar();
            if (solicitud != null) return solicitud;
            if (terminado()) return null;
            Thread.yield();
        }
    }

    public synchronized void usuarioTermino() {
        if (usuariosActivos == 0) throw new IllegalStateException("No hay usuarios activos");
        usuariosActivos--;
        notifyAll();
    }
    public synchronized int getUsuariosActivos() { return usuariosActivos; }
    public synchronized int getCantidadSolicitudes() { return cola.size(); }
    public synchronized boolean estaVacio() { return cola.isEmpty(); }
    public synchronized boolean terminado() { return usuariosActivos == 0 && cola.isEmpty(); }
}

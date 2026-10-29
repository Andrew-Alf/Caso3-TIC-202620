/**
 * PERSONA 2: administra los procesadores del sistema.
 * Firma acordada: reservar(cantidad, mv) -> true si pudo reservar todo, false si no;
 *                 liberar(mv); libres(); estaTodoLibre() (para validacion final).
 */
public class ControladorProcesadores {

    private final int total;

    public ControladorProcesadores(int total) {
        this.total = total;
    }

    public int getTotal() { return total; }

    // TODO(Persona 2): estructura de asignacion (p. ej. MaquinaVirtual[] duenos)
    public synchronized boolean reservar(int cantidad, MaquinaVirtual mv) { return true; }

    public synchronized void liberar(MaquinaVirtual mv) { }

    public synchronized int libres() { return total; }

    public synchronized boolean estaTodoLibre() { return true; }
}

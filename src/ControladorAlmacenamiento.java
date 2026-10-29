/**
 * PERSONA 2: administra el almacenamiento (bloques) del sistema.
 * Firma acordada: reservar(almacenamientoGiB, mv), liberar(mv), libres(), estaTodoLibre().
 */
public class ControladorAlmacenamiento {

    private final int almacenamientoGiB;
    private final int bloqueKiB;

    public ControladorAlmacenamiento(int almacenamientoGiB, int bloqueKiB) {
        this.almacenamientoGiB = almacenamientoGiB;
        this.bloqueKiB = bloqueKiB;
    }

    // TODO(Persona 2): mapa de bloques libres/asignados
    public synchronized boolean reservar(int gib, MaquinaVirtual mv) { return true; }

    public synchronized void liberar(MaquinaVirtual mv) { }

    public synchronized int libres() { return almacenamientoGiB; }

    public synchronized boolean estaTodoLibre() { return true; }
}

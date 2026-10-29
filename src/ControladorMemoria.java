/**
 * PERSONA 2: administra la memoria (paginas) del sistema.
 * Firma acordada: reservar(memoriaGiB, mv), liberar(mv), libres(), estaTodoLibre().
 */
public class ControladorMemoria {

    private final int memoriaGiB;
    private final int paginaKiB;

    public ControladorMemoria(int memoriaGiB, int paginaKiB) {
        this.memoriaGiB = memoriaGiB;
        this.paginaKiB = paginaKiB;
    }

    // TODO(Persona 2): mapa de paginas libres/asignadas
    public synchronized boolean reservar(int gib, MaquinaVirtual mv) { return true; }

    public synchronized void liberar(MaquinaVirtual mv) { }

    public synchronized int libres() { return memoriaGiB; }

    public synchronized boolean estaTodoLibre() { return true; }
}

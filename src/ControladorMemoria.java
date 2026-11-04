/** Administra paginas de memoria fisica y la MV propietaria de cada pagina. */
public class ControladorMemoria {
    private final MaquinaVirtual[] duenosPaginas;
    private final int paginaKiB;
    private int cantidadLibres;

    public ControladorMemoria(int memoriaGiB, int paginaKiB) {
        if (memoriaGiB <= 0 || paginaKiB <= 0)
            throw new IllegalArgumentException("Parametros de memoria invalidos");
        this.paginaKiB = paginaKiB;
        long paginas = ((long) memoriaGiB * 1048576L) / paginaKiB;
        if (paginas < 1 || paginas > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Cantidad de paginas no representable");
        duenosPaginas = new MaquinaVirtual[(int) paginas];
        cantidadLibres = (int) paginas;
    }

    /** Redondea hacia arriba a paginas completas para no asignar menos de lo pedido. */
    public int calcularPaginas(int memoriaGiB) {
        if (memoriaGiB <= 0) throw new IllegalArgumentException("Memoria solicitada no positiva");
        long kiB = (long) memoriaGiB * 1048576L;
        long paginas = (kiB + paginaKiB - 1L) / paginaKiB;
        if (paginas > Integer.MAX_VALUE) throw new IllegalArgumentException("Demasiadas paginas");
        return (int) paginas;
    }

    public synchronized boolean reservar(int gib, MaquinaVirtual mv) {
        if (mv == null) throw new IllegalArgumentException("MV nula");
        int requeridas = calcularPaginas(gib);
        if (requeridas > cantidadLibres) return false;
        int restantes = requeridas;
        for (int i = 0; i < duenosPaginas.length && restantes > 0; i++) {
            if (duenosPaginas[i] == null) {
                duenosPaginas[i] = mv;
                restantes--;
            }
        }
        cantidadLibres -= requeridas;
        return true;
    }

    public synchronized void liberar(MaquinaVirtual mv) {
        for (int i = 0; i < duenosPaginas.length; i++) {
            if (duenosPaginas[i] == mv) {
                duenosPaginas[i] = null;
                cantidadLibres++;
            }
        }
    }
    /** Numero de paginas libres. */
    public synchronized int libres() { return cantidadLibres; }
    public synchronized boolean estaTodoLibre() {
        if (cantidadLibres != duenosPaginas.length) return false;
        for (MaquinaVirtual dueno : duenosPaginas) if (dueno != null) return false;
        return true;
    }
}

/** Administra bloques de disco individuales y la MV propietaria de cada bloque. */
public class ControladorAlmacenamiento {
    private final MaquinaVirtual[] duenosBloques;
    private final int bloqueKiB;
    private int cantidadLibres;

    public ControladorAlmacenamiento(int almacenamientoGiB, int bloqueKiB) {
        if (almacenamientoGiB <= 0 || bloqueKiB <= 0)
            throw new IllegalArgumentException("Parametros de almacenamiento invalidos");
        this.bloqueKiB = bloqueKiB;
        long bloques = ((long) almacenamientoGiB * 1048576L) / bloqueKiB;
        if (bloques < 1 || bloques > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Cantidad de bloques no representable");
        duenosBloques = new MaquinaVirtual[(int) bloques];
        cantidadLibres = (int) bloques;
    }

    /** Redondea hacia arriba a bloques completos para no asignar menos de lo pedido. */
    public int calcularBloques(int almacenamientoGiB) {
        if (almacenamientoGiB <= 0)
            throw new IllegalArgumentException("Disco solicitado no positivo");
        long kiB = (long) almacenamientoGiB * 1048576L;
        long bloques = (kiB + bloqueKiB - 1L) / bloqueKiB;
        if (bloques > Integer.MAX_VALUE) throw new IllegalArgumentException("Demasiados bloques");
        return (int) bloques;
    }

    public synchronized boolean reservar(int gib, MaquinaVirtual mv) {
        if (mv == null) throw new IllegalArgumentException("MV nula");
        int requeridos = calcularBloques(gib);
        if (requeridos > cantidadLibres) return false;
        int restantes = requeridos;
        for (int i = 0; i < duenosBloques.length && restantes > 0; i++) {
            if (duenosBloques[i] == null) {
                duenosBloques[i] = mv;
                restantes--;
            }
        }
        cantidadLibres -= requeridos;
        return true;
    }

    public synchronized void liberar(MaquinaVirtual mv) {
        for (int i = 0; i < duenosBloques.length; i++) {
            if (duenosBloques[i] == mv) {
                duenosBloques[i] = null;
                cantidadLibres++;
            }
        }
    }
    /** Numero de bloques libres. */
    public synchronized int libres() { return cantidadLibres; }
    public synchronized boolean estaTodoLibre() {
        if (cantidadLibres != duenosBloques.length) return false;
        for (MaquinaVirtual dueno : duenosBloques) if (dueno != null) return false;
        return true;
    }
}

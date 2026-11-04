/** Administra procesadores individuales y la MV propietaria de cada uno. */
public class ControladorProcesadores {
    private final MaquinaVirtual[] duenos;
    private int cantidadLibres;

    public ControladorProcesadores(int total) {
        if (total <= 0) throw new IllegalArgumentException("CPU total no positivo");
        duenos = new MaquinaVirtual[total];
        cantidadLibres = total;
    }
    public int getTotal() { return duenos.length; }

    /** Reserva exactamente cantidad procesadores, o no reserva ninguno. */
    public synchronized boolean reservar(int cantidad, MaquinaVirtual mv) {
        if (mv == null || cantidad <= 0) throw new IllegalArgumentException("Reserva CPU invalida");
        if (cantidad > cantidadLibres) return false;
        int restantes = cantidad;
        for (int i = 0; i < duenos.length && restantes > 0; i++) {
            if (duenos[i] == null) {
                duenos[i] = mv;
                restantes--;
            }
        }
        cantidadLibres -= cantidad;
        return true;
    }

    public synchronized void liberar(MaquinaVirtual mv) {
        for (int i = 0; i < duenos.length; i++) {
            if (duenos[i] == mv) {
                duenos[i] = null;
                cantidadLibres++;
            }
        }
    }
    public synchronized int libres() { return cantidadLibres; }
    public synchronized boolean estaTodoLibre() {
        if (cantidadLibres != duenos.length) return false;
        for (MaquinaVirtual dueno : duenos) if (dueno != null) return false;
        return true;
    }
}


public class Solicitud {

    private final String id;
    private final int procesadores;
    private final int memoriaGiB;
    private final int almacenamientoGiB;

    public Solicitud(String id, int procesadores,
                     int memoriaGiB, int almacenamientoGiB) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El identificador no puede estar vacio.");
        }

        if (procesadores <= 0 || memoriaGiB <= 0
                || almacenamientoGiB <= 0) {
            throw new IllegalArgumentException(
                "Los recursos deben ser positivos.");
        }

        this.id = id;
        this.procesadores = procesadores;
        this.memoriaGiB = memoriaGiB;
        this.almacenamientoGiB = almacenamientoGiB;
    }

    public String getId() {
        return id;
    }

    public int getProcesadores() {
        return procesadores;
    }

    public int getMemoriaGiB() {
        return memoriaGiB;
    }

    public int getAlmacenamientoGiB() {
        return almacenamientoGiB;
    }

    @Override
    public String toString() {
        return "Solicitud " + id
                + " | CPU: " + procesadores
                + " | RAM: " + memoriaGiB + " GiB"
                + " | Disco: " + almacenamientoGiB + " GiB";
    }
}

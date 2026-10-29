import java.util.Random;

/**
 * Thread productor. Genera (base * id) solicitudes y las deposita en el buzon de entrada.
 */
public class Usuario extends Thread {

    private final int id;
    private final int numSolicitudes;
    private final BuzonEntrada buzon;
    private final Configuracion config;
    private final Random random = new Random();

    public Usuario(int id, Configuracion config, BuzonEntrada buzon) {
        super("Usuario-" + id);
        this.id = id;
        this.config = config;
        this.buzon = buzon;
        this.numSolicitudes = config.getBase() * id;
    }

    @Override
    public void run() {
        try {
            for (int seq = 1; seq <= numSolicitudes; seq++) {
                Solicitud s = generarSolicitud(seq);
                buzon.depositar(s);
                System.out.println("[Usuario " + id + "] deposito " + s);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Siempre reporta su terminacion para que los gestores puedan finalizar.
            buzon.usuarioTermino();
            System.out.println("[Usuario " + id + "] termino");
        }
    }

    private Solicitud generarSolicitud(int seq) {
        String idSolicitud = id + "-" + seq;

        // Procesadores: entre 1 y el entero mas cercano a 3/4 de la capacidad
        int maxCpu = tresCuartos(config.getProcesadores());
        int cpu = aleatorio(1, maxCpu);

        // Memoria: entre 1 y 3/4 de la capacidad, redondeado a la potencia de 2 mas alta <= valor
        int maxMem = tresCuartos(config.getMemoria());
        int mem = Integer.highestOneBit(aleatorio(1, maxMem));

        // Almacenamiento: entre 1 y 3/4 de la capacidad, redondeado al multiplo de 2 mas alto <= valor
        int maxDisco = tresCuartos(config.getAlmacenamiento());
        int disco = aleatorio(1, maxDisco);
        disco = disco - (disco % 2);
        if (disco < 1) disco = 1; // caso borde: si el valor generado fue 1, se deja en 1

        return new Solicitud(idSolicitud, cpu, mem, disco);
    }

    private int tresCuartos(int capacidad) {
        return Math.max(1, (int) Math.round(0.75 * capacidad));
    }

    /** Entero aleatorio en [min, max], ambos inclusive. */
    private int aleatorio(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}

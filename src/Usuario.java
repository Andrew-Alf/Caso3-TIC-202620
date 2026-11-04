import java.util.Random;

/** Thread productor. Genera base * id solicitudes. */
public class Usuario extends Thread {
    private final int id, numSolicitudes;
    private final BuzonEntrada buzon;
    private final Configuracion config;
    private final Random random = new Random();

    public Usuario(int id, Configuracion config, BuzonEntrada buzon) {
        super("Usuario-" + id);
        if (id <= 0) throw new IllegalArgumentException("ID de usuario invalido");
        this.id = id;
        this.config = config;
        this.buzon = buzon;
        this.numSolicitudes = Math.multiplyExact(config.getBase(), id);
    }

    @Override public void run() {
        try {
            for (int seq = 1; seq <= numSolicitudes; seq++) {
                Solicitud solicitud = generarSolicitud(seq);
                buzon.depositar(solicitud);
                System.out.println("[Usuario " + id + "] deposito " + solicitud);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Usuario " + id + "] interrumpido");
        } finally {
            buzon.usuarioTermino();
            System.out.println("[Usuario " + id + "] termino");
        }
    }

    private Solicitud generarSolicitud(int secuencial) {
        int cpu = aleatorio(1, tresCuartos(config.getProcesadores()));
        int generadoMemoria = aleatorio(1, tresCuartos(config.getMemoria()));
        int memoria = Integer.highestOneBit(generadoMemoria); // Potencia de 2 inferior o igual.
        int generadoDisco = aleatorio(1, tresCuartos(config.getAlmacenamiento()));
        // Multiplo de 2 inferior o igual, conservando 1 como caso minimo valido.
        int disco = generadoDisco == 1 ? 1 : generadoDisco - (generadoDisco % 2);
        return new Solicitud(id + "-" + secuencial, cpu, memoria, disco);
    }

    private int tresCuartos(int capacidad) {
        return Math.max(1, (int) Math.round(0.75 * capacidad));
    }

    private int aleatorio(int minimo, int maximo) {
        return minimo + random.nextInt(maximo - minimo + 1);
    }
}

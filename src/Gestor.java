import java.util.ArrayList;
import java.util.List;

/** Thread consumidor que crea una MV por solicitud y respeta el umbral de espera. */
public class Gestor extends Thread {
    private final int id;
    private final BuzonEntrada buzon;
    private final ZonaEspera zona;
    private final ControladorProcesadores cpu;
    private final ControladorMemoria ram;
    private final ControladorAlmacenamiento disco;
    private final List<MaquinaVirtual> maquinas = new ArrayList<>();

    public Gestor(int id, BuzonEntrada buzon, ZonaEspera zona,
            ControladorProcesadores cpu, ControladorMemoria ram,
            ControladorAlmacenamiento disco) {
        super("Gestor-" + id);
        this.id = id;
        this.buzon = buzon;
        this.zona = zona;
        this.cpu = cpu;
        this.ram = ram;
        this.disco = disco;
    }

    @Override public void run() {
        try {
            while (true) {
                // Antes de CADA retiro, controlar las MVs pendientes.
                zona.esperarSiSaturada();
                // Retiro NO bloqueante: no queda un gestor esperando una solicitud
                // sin volver a consultar el umbral cuando llegue otra.
                Solicitud s = buzon.intentarRetirar();
                if (s == null) {
                    if (buzon.terminado()) break;
                    Thread.yield(); // espera semi-activa fuera de monitores.
                    continue;
                }
                MaquinaVirtual mv = new MaquinaVirtual(s, cpu, ram, disco, zona);
                maquinas.add(mv);
                mv.start();
                System.out.println("[Gestor " + id + "] creo MV para " + s.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Gestor " + id + "] interrumpido");
        } finally {
            // Espera la finalizacion de TODAS las MVs que creo. Maneja interrupcion
            // sin dejar MVs abandonadas que harian fallar la validacion de Main.
            boolean interrumpido = Thread.interrupted();
            for (MaquinaVirtual mv : maquinas) {
                boolean terminado = false;
                while (!terminado) {
                    try {
                        mv.join();
                        terminado = true;
                    } catch (InterruptedException e) {
                        interrumpido = true;
                    }
                }
            }
            if (interrumpido) Thread.currentThread().interrupt();
            System.out.println("[Gestor " + id + "] termino (MVs creadas: " + maquinas.size() + ")");
        }
    }

    public int getNumeroMaquinasCreadas() {
        // Consultar solo DESPUES de join() del gestor.
        return maquinas.size();
    }

    public int getNumeroMaquinasCompletadas() {
        // Tras el join() del gestor, todas las MVs finalizaron.
        int completas = 0;
        for (MaquinaVirtual mv : maquinas) if (mv.ejecucionCompleta()) completas++;
        return completas;
    }
}

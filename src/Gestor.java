import java.util.ArrayList;
import java.util.List;

/**
 * Thread gestor de solicitudes. Por cada solicitud del buzon crea una MV (thread) y la inicia.
 *
 * Antes de tomar cada solicitud consulta la zona de espera: si las MVs pendientes alcanzaron
 * el umbral, se bloquea hasta que TODAS las pendientes se procesen (espera total).
 */
public class Gestor extends Thread {

    private final int id;
    private final BuzonEntrada buzon;
    private final ZonaEspera zona;
    private final ControladorProcesadores ctrlProcesadores;
    private final ControladorMemoria ctrlMemoria;
    private final ControladorAlmacenamiento ctrlAlmacenamiento;
    private final List<MaquinaVirtual> maquinas = new ArrayList<>(); // solo la toca este thread

    public Gestor(int id, BuzonEntrada buzon, ZonaEspera zona,
                  ControladorProcesadores ctrlProcesadores,
                  ControladorMemoria ctrlMemoria,
                  ControladorAlmacenamiento ctrlAlmacenamiento) {
        super("Gestor-" + id);
        this.id = id;
        this.buzon = buzon;
        this.zona = zona;
        this.ctrlProcesadores = ctrlProcesadores;
        this.ctrlMemoria = ctrlMemoria;
        this.ctrlAlmacenamiento = ctrlAlmacenamiento;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 1. Revisar MVs pendientes. Si >= umbral, espera hasta que la zona quede vacia.
                zona.esperarSiSaturada();

                // 2. Tomar la siguiente solicitud (espera semi-activa si el buzon esta vacio).
                Solicitud s = buzon.retirar();
                if (s == null) {
                    break; // buzon vacio y sin usuarios activos: el gestor termina
                }

                // 3. Crear la MV y arrancarla.
                MaquinaVirtual mv = new MaquinaVirtual(s, ctrlProcesadores, ctrlMemoria,
                                                       ctrlAlmacenamiento, zona);
                maquinas.add(mv);
                mv.start();
                System.out.println("[Gestor " + id + "] creo MV para " + s.getId());
            }
            // Antes de terminar espera a las MVs que el mismo creo.
            for (MaquinaVirtual mv : maquinas) {
                mv.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[Gestor " + id + "] termino");
    }
}

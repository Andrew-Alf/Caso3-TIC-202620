import java.io.IOException;

/**
 * Crea los componentes, los conecta, arranca los threads y valida el estado final.
 * Uso: java Main [ruta_config]   (por defecto "config.txt")
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        String ruta = args.length > 0 ? args[0] : "config.txt";
        Configuracion cfg;
        try {
            cfg = Configuracion.cargar(ruta);
        } catch (IOException e) {
            System.err.println("Error leyendo la configuracion: " + e.getMessage());
            return;
        }

        BuzonEntrada buzon = new BuzonEntrada(cfg.getTam1(), cfg.getNu());
        ZonaEspera zona = new ZonaEspera(cfg.getTam2());
        ControladorProcesadores ctrlProc = new ControladorProcesadores(cfg.getProcesadores());
        ControladorMemoria ctrlMem = new ControladorMemoria(cfg.getMemoria(), cfg.getPagina());
        ControladorAlmacenamiento ctrlAlm =
                new ControladorAlmacenamiento(cfg.getAlmacenamiento(), cfg.getBloque());

        Gestor[] gestores = new Gestor[cfg.getNg()];
        for (int i = 0; i < gestores.length; i++) {
            gestores[i] = new Gestor(i + 1, buzon, zona, ctrlProc, ctrlMem, ctrlAlm);
        }
        Usuario[] usuarios = new Usuario[cfg.getNu()];
        for (int i = 0; i < usuarios.length; i++) {
            usuarios[i] = new Usuario(i + 1, cfg, buzon);
        }

        for (Gestor g : gestores) g.start();
        for (Usuario u : usuarios) u.start();

        for (Usuario u : usuarios) u.join();
        for (Gestor g : gestores) g.join(); // cada gestor espera a sus propias MVs

        System.out.println("=== Validacion final ===");
        System.out.println("Buzon vacio: " + buzon.estaVacio());
        System.out.println("Usuarios activos: " + buzon.getUsuariosActivos());
        System.out.println("MVs pendientes en zona de espera: " + zona.pendientes());
        System.out.println("Procesadores libres: " + ctrlProc.estaTodoLibre());
        System.out.println("Memoria libre: " + ctrlMem.estaTodoLibre());
        System.out.println("Almacenamiento libre: " + ctrlAlm.estaTodoLibre());
    }
}

import java.io.IOException;

/** Punto de entrada: inicializa el sistema, lanza threads y valida terminacion. */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        String ruta = args.length > 0 ? args[0] : "config.txt";
        final Configuracion cfg;
        try {
            cfg = Configuracion.cargar(ruta);
        } catch (IOException e) {
            System.err.println("Error leyendo configuracion: " + e.getMessage());
            return;
        }
        BuzonEntrada buzon = new BuzonEntrada(cfg.getTam1(), cfg.getNu());
        ZonaEspera zona = new ZonaEspera(cfg.getTam2());
        ControladorProcesadores cpu = new ControladorProcesadores(cfg.getProcesadores());
        ControladorMemoria ram = new ControladorMemoria(cfg.getMemoria(), cfg.getPagina());
        ControladorAlmacenamiento disco =
                new ControladorAlmacenamiento(cfg.getAlmacenamiento(), cfg.getBloque());

        Gestor[] gestores = new Gestor[cfg.getNg()];
        for (int i = 0; i < gestores.length; i++)
            gestores[i] = new Gestor(i + 1, buzon, zona, cpu, ram, disco);
        Usuario[] usuarios = new Usuario[cfg.getNu()];
        for (int i = 0; i < usuarios.length; i++)
            usuarios[i] = new Usuario(i + 1, cfg, buzon);

        for (Gestor gestor : gestores) gestor.start();
        for (Usuario usuario : usuarios) usuario.start();
        for (Usuario usuario : usuarios) usuario.join();
        for (Gestor gestor : gestores) gestor.join(); // Incluye join a todas sus MVs.

        long esperado = (long) cfg.getBase() * cfg.getNu() * (cfg.getNu() + 1L) / 2L;
        long creadas = 0;
        long completadas = 0;
        for (Gestor gestor : gestores) {
            creadas += gestor.getNumeroMaquinasCreadas();
            completadas += gestor.getNumeroMaquinasCompletadas();
        }
        boolean correcto = creadas == completadas && buzon.estaVacio()
                && buzon.getUsuariosActivos() == 0
                && zona.pendientes() == 0 && cpu.estaTodoLibre()
                && ram.estaTodoLibre() && disco.estaTodoLibre() && creadas == esperado;

        System.out.println("\n=== VALIDACION FINAL ===");
        System.out.println("Solicitudes esperadas / MVs creadas / MVs completadas: "
                + esperado + " / " + creadas + " / " + completadas);
        System.out.println("Buzon vacio: " + buzon.estaVacio());
        System.out.println("Usuarios activos: " + buzon.getUsuariosActivos());
        System.out.println("MVs pendientes: " + zona.pendientes());
        System.out.println("Procesadores libres: " + cpu.libres() + "/" + cpu.getTotal());
        System.out.println("Memoria completamente libre: " + ram.estaTodoLibre());
        System.out.println("Almacenamiento completamente libre: " + disco.estaTodoLibre());
        System.out.println("VALIDACION: " + (correcto ? "CORRECTA" : "FALLO"));
        if (!correcto) throw new IllegalStateException("Error en la terminacion o asignacion");
    }
}

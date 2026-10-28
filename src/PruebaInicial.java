
public class PruebaInicial {

    public static void main(String[] args) throws Exception {

        Configuracion config =
                Configuracion.cargar("config.txt");

        System.out.println("=== CONFIGURACION ===");
        System.out.println("Usuarios: " + config.getNu());
        System.out.println("Gestores: " + config.getNg());
        System.out.println("Capacidad buzon: "
                + config.getTam1());

        Solicitud solicitud =
                new Solicitud("2-3", 2, 4, 8);

        System.out.println("\n=== SOLICITUD ===");
        System.out.println(solicitud);
    }
}

/** Prueba basica de lectura y objetos; la prueba real de concurrencia usa Main. */
public class PruebaInicial {
    public static void main(String[] args) throws Exception {
        Configuracion c = Configuracion.cargar("config.txt");
        System.out.println("Usuarios: " + c.getNu() + " | Gestores: " + c.getNg()
                + " | Buzon: " + c.getTam1());
        System.out.println(new Solicitud("2-3", 2, 4, 8));
    }
}

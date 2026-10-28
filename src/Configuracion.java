
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Configuracion {

    private final int nu;
    private final int base;
    private final int ng;
    private final int tam1;
    private final int tam2;
    private final int procesadores;
    private final int memoriaGiB;
    private final int paginaKiB;
    private final int almacenamientoGiB;
    private final int bloqueKiB;

    private Configuracion(int nu, int base, int ng,
                          int tam1, int tam2,
                          int procesadores, int memoriaGiB,
                          int paginaKiB, int almacenamientoGiB,
                          int bloqueKiB) {

        this.nu = nu;
        this.base = base;
        this.ng = ng;
        this.tam1 = tam1;
        this.tam2 = tam2;
        this.procesadores = procesadores;
        this.memoriaGiB = memoriaGiB;
        this.paginaKiB = paginaKiB;
        this.almacenamientoGiB = almacenamientoGiB;
        this.bloqueKiB = bloqueKiB;
    }

    public static Configuracion cargar(String ruta)
            throws IOException {

        Properties propiedades = new Properties();

        try (BufferedReader lector =
                     Files.newBufferedReader(
                         Paths.get(ruta),
                         StandardCharsets.UTF_8)) {

            propiedades.load(lector);
        }

        return new Configuracion(
            leerEntero(propiedades, "nu"),
            leerEntero(propiedades, "base"),
            leerEntero(propiedades, "ng"),
            leerEntero(propiedades, "tam1"),
            leerEntero(propiedades, "tam2"),
            leerEntero(propiedades, "procesadores"),
            leerEntero(propiedades, "memoria"),
            leerEntero(propiedades, "pagina"),
            leerEntero(propiedades, "almacenamiento"),
            leerEntero(propiedades, "bloque")
        );
    }

    private static int leerEntero(Properties p, String clave) {

        String valor = p.getProperty(clave);

        if (valor == null) {
            throw new IllegalArgumentException(
                "Falta el parametro: " + clave);
        }

        try {
            int numero = Integer.parseInt(valor.trim());

            if (numero <= 0) {
                throw new IllegalArgumentException(
                    "El parametro " + clave
                    + " debe ser positivo.");
            }

            return numero;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "El parametro " + clave
                + " debe ser un entero.", e);
        }
    }

    public int getNu() {
        return nu;
    }

    public int getBase() {
        return base;
    }

    public int getNg() {
        return ng;
    }

    public int getTam1() {
        return tam1;
    }

    public int getTam2() {
        return tam2;
    }

    public int getProcesadores() {
        return procesadores;
    }

    public int getMemoriaGiB() {
        return memoriaGiB;
    }

    public int getPaginaKiB() {
        return paginaKiB;
    }

    public int getAlmacenamientoGiB() {
        return almacenamientoGiB;
    }

    public int getBloqueKiB() {
        return bloqueKiB;
    }
}

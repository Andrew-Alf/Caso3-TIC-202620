import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lee el archivo de texto con los parametros del sistema (formato clave=valor).
 * Las lineas vacias y las que empiezan con # se ignoran.
 * Es inmutable: despues de cargarla solo se consulta, asi que no necesita sincronizacion.
 */
public class Configuracion {

    private final int nu;             // numero de usuarios
    private final int base;           // numero base de solicitudes por usuario
    private final int ng;             // numero de gestores
    private final int tam1;           // capacidad del buzon de entrada
    private final int tam2;           // capacidad de la zona de espera (= umbral)
    private final int procesadores;   // numero de procesadores
    private final int memoria;        // memoria total en GiB
    private final int pagina;         // tamano de pagina en KiB
    private final int almacenamiento; // almacenamiento total en GiB
    private final int bloque;         // tamano de bloque en KiB

    private Configuracion(Map<String, Integer> v) {
        this.nu = v.get("nu");
        this.base = v.get("base");
        this.ng = v.get("ng");
        this.tam1 = v.get("tam1");
        this.tam2 = v.get("tam2");
        this.procesadores = v.get("procesadores");
        this.memoria = v.get("memoria");
        this.pagina = v.get("pagina");
        this.almacenamiento = v.get("almacenamiento");
        this.bloque = v.get("bloque");
    }

    public static Configuracion cargar(String ruta) throws IOException {
        Map<String, Integer> valores = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split("=");
                if (partes.length != 2) {
                    throw new IOException("Linea invalida en la configuracion: " + linea);
                }
                try {
                    valores.put(partes[0].trim(), Integer.parseInt(partes[1].trim()));
                } catch (NumberFormatException e) {
                    throw new IOException("Valor no numerico en la linea: " + linea);
                }
            }
        }
        String[] requeridos = {"nu", "base", "ng", "tam1", "tam2", "procesadores",
                               "memoria", "pagina", "almacenamiento", "bloque"};
        for (String clave : requeridos) {
            if (!valores.containsKey(clave)) {
                throw new IOException("Falta el parametro '" + clave + "' en la configuracion");
            }
        }
        return new Configuracion(valores);
    }

    public int getNu() { return nu; }
    public int getBase() { return base; }
    public int getNg() { return ng; }
    public int getTam1() { return tam1; }
    public int getTam2() { return tam2; }
    public int getProcesadores() { return procesadores; }
    public int getMemoria() { return memoria; }
    public int getPagina() { return pagina; }
    public int getAlmacenamiento() { return almacenamiento; }
    public int getBloque() { return bloque; }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Parametros de ejecucion cargados de un archivo clave=valor. */
public class Configuracion {
    private final int nu, base, ng, tam1, tam2, procesadores;
    private final int memoria, pagina, almacenamiento, bloque;

    private Configuracion(Map<String, Integer> v) {
        nu = v.get("nu");
        base = v.get("base");
        ng = v.get("ng");
        tam1 = v.get("tam1");
        tam2 = v.get("tam2");
        procesadores = v.get("procesadores");
        memoria = v.get("memoria");
        pagina = v.get("pagina");
        almacenamiento = v.get("almacenamiento");
        bloque = v.get("bloque");
    }

    public static Configuracion cargar(String ruta) throws IOException {
        Map<String, Integer> valores = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ruta), StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = br.readLine()) != null) {
                numero++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split("=", -1);
                if (partes.length != 2 || partes[0].trim().isEmpty()) {
                    throw new IOException("Linea invalida " + numero + ": " + linea);
                }
                String clave = partes[0].trim();
                try {
                    int valor = Integer.parseInt(partes[1].trim());
                    if (valores.putIfAbsent(clave, valor) != null) {
                        throw new IOException("Parametro duplicado: " + clave);
                    }
                } catch (NumberFormatException e) {
                    throw new IOException("Valor no entero para " + clave + " en linea " + numero, e);
                }
            }
        }
        String[] requeridos = {"nu", "base", "ng", "tam1", "tam2", "procesadores",
            "memoria", "pagina", "almacenamiento", "bloque"};
        for (String clave : requeridos) {
            Integer valor = valores.get(clave);
            if (valor == null) throw new IOException("Falta el parametro " + clave);
            if (valor <= 0) throw new IOException("El parametro " + clave + " debe ser positivo");
        }
        if ((long) valores.get("memoria") * 1048576L < valores.get("pagina")) {
            throw new IOException("El tamano de pagina excede la memoria total");
        }
        if ((long) valores.get("almacenamiento") * 1048576L < valores.get("bloque")) {
            throw new IOException("El tamano de bloque excede el almacenamiento total");
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
    // Alias para compatibilidad con el diseno inicial.
    public int getMemoriaGiB() { return memoria; }
    public int getPaginaKiB() { return pagina; }
    public int getAlmacenamientoGiB() { return almacenamiento; }
    public int getBloqueKiB() { return bloque; }
}

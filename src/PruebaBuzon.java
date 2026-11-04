import java.util.HashSet;
import java.util.Set;

/** Prueba de 4 productores y 4 consumidores usando solo primitivas basicas de Java. */
public class PruebaBuzon {
    public static void main(String[] args) throws Exception {
        final int productores = 4;
        final int porProductor = 50;
        final BuzonEntrada buzon = new BuzonEntrada(2, productores);
        final Set<String> ids = new HashSet<>();
        Thread[] usuarios = new Thread[productores];
        Thread[] gestores = new Thread[4];
        for (int i = 0; i < usuarios.length; i++) {
            final int id = i + 1;
            usuarios[i] = new Thread(() -> {
                try {
                    for (int j = 1; j <= porProductor; j++)
                        buzon.depositar(new Solicitud(id + "-" + j, 1, 1, 1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    buzon.usuarioTermino();
                }
            });
        }
        for (int i = 0; i < gestores.length; i++) {
            gestores[i] = new Thread(() -> {
                while (true) {
                    Solicitud s = buzon.intentarRetirar();
                    if (s != null) {
                        synchronized (ids) {
                            if (!ids.add(s.getId()))
                                throw new AssertionError("Solicitud duplicada " + s.getId());
                        }
                    } else if (buzon.terminado()) {
                        break;
                    } else {
                        Thread.yield();
                    }
                }
            });
        }
        for (Thread g : gestores) g.start();
        for (Thread u : usuarios) u.start();
        for (Thread u : usuarios) u.join();
        for (Thread g : gestores) g.join();
        if (ids.size() != productores * porProductor || !buzon.terminado())
            throw new AssertionError("Solicitudes perdidas: " + ids.size());
        System.out.println("PRUEBA BUZON: CORRECTA (" + ids.size() + " solicitudes unicas)");
    }
}

/**
 * PERSONA 2: zona de espera de MVs que no pudieron reservar todos sus recursos.
 *
 * Contrato con Persona 1 (el Gestor solo usa estos metodos):
 *   - esperarSiSaturada(): si pendientes >= umbral, bloquea hasta que pendientes == 0.
 *       La revision y la espera son atomicas (mismo monitor), asi no hay carrera.
 *   - pendientes(): cantidad actual de MVs dormidas (para validacion final).
 *
 * Contrato con las MVs (lado de Persona 2):
 *   - dormir(mv) / despertar(): a cargo de Persona 2.
 *   - Cuando pendientes baje a 0 hay que hacer notifyAll() para liberar al gestor.
 */
public class ZonaEspera {

    private final int umbral;
    private int pendientes = 0;

    public ZonaEspera(int umbral) {
        this.umbral = umbral;
    }

    public int getUmbral() { return umbral; }

    public synchronized int pendientes() { return pendientes; }

    /** Usado por el Gestor. */
    public synchronized void esperarSiSaturada() throws InterruptedException {
        if (pendientes >= umbral) {
            while (pendientes > 0) {
                wait();
            }
        }
    }

    // TODO(Persona 2): metodo para que una MV se duerma en la zona (pendientes++, wait,
    //                  pendientes-- y notifyAll si pendientes == 0).
    // TODO(Persona 2): metodo para despertar MVs cuando se liberan recursos.
}

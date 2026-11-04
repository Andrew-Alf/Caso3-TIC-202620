/** Prueba determinista de asignaciones atomicas por controlador. */
public class PruebaControladores {
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    public static void main(String[] args) {
        ZonaEspera zona = new ZonaEspera(1);
        ControladorProcesadores cpu = new ControladorProcesadores(2);
        ControladorMemoria ram = new ControladorMemoria(2, 1048576); // 2 paginas de 1 GiB
        ControladorAlmacenamiento disco = new ControladorAlmacenamiento(4, 1048576);
        MaquinaVirtual a = new MaquinaVirtual(new Solicitud("A", 1, 1, 1), cpu, ram, disco, zona);
        MaquinaVirtual b = new MaquinaVirtual(new Solicitud("B", 1, 1, 1), cpu, ram, disco, zona);

        comprobar(cpu.reservar(2, a), "A debe reservar 2 CPU");
        comprobar(!cpu.reservar(1, b), "B no debe reservar CPU agotadas");
        comprobar(cpu.libres() == 0, "CPU libres inconsistente");
        cpu.liberar(a);
        comprobar(cpu.estaTodoLibre(), "CPU no regresaron a estado inicial");

        comprobar(ram.reservar(1, a), "A debe reservar una pagina");
        comprobar(!ram.reservar(2, b), "Reserva de RAM debe ser todo o nada");
        comprobar(ram.libres() == 1, "Reserva fallida de RAM altero disponibilidad");
        ram.liberar(a);
        comprobar(ram.reservar(2, b), "B debe reservar dos paginas");
        ram.liberar(b);
        comprobar(ram.estaTodoLibre(), "No se liberaron paginas");

        comprobar(disco.reservar(3, a), "A debe reservar tres bloques");
        comprobar(!disco.reservar(2, b), "No debe asignar disco parcialmente");
        comprobar(disco.libres() == 1, "Reserva fallida de disco altero disponibilidad");
        disco.liberar(a);
        comprobar(disco.estaTodoLibre(), "No se liberaron bloques");

        ControladorMemoria otro = new ControladorMemoria(2, 300000);
        comprobar(otro.calcularPaginas(1) == 4, "Redondeo de paginas incorrecto");
        System.out.println("PRUEBA CONTROLADORES: CORRECTA");
    }
}

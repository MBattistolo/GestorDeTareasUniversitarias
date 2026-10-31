import Exceptions.EFueraRango;
import java.time.LocalDate;
import java.util.List;

/** Demostración automática de consola. No necesita interfaz gráfica. */
public class MainDemo {
    public static void main(String[] args) {
        try {
            ejecutarDemo();
        } catch (EFueraRango e) {
            throw new IllegalStateException("Los datos de la demostración son inválidos.", e);
        }
    }

    private static void ejecutarDemo() throws EFueraRango {
        GestorTareas gestor = new GestorTareas();
        Asignatura estructuras = new Asignatura("ED", "Estructura de Datos");
        Asignatura calculo = new Asignatura("CAL", "Cálculo");
        LocalDate hoy = LocalDate.now();

        System.out.println("=== GESTOR DE TAREAS UNIVERSITARIAS ===");
        System.out.println("Prioridad: fecha anterior > mayor peso > primer registro.\n");
        System.out.println("1. Registro de cinco tareas en un orden diferente al de atención:");
        gestor.registrarTarea(1, "Proyecto de estructuras", "Implementar el heap",
                hoy.plusDays(3), 30, 60, estructuras);
        gestor.registrarTarea(2, "Taller de programación", "Resolver ejercicios",
                hoy.plusDays(1), 10, 45, estructuras);
        gestor.registrarTarea(3, "Cuestionario de cálculo", "Repasar derivadas",
                hoy.plusDays(3), 5, 30, calculo);
        gestor.registrarTarea(4, "Ejercicio adicional", "Empate con el taller",
                hoy.plusDays(1), 10, 120, estructuras);
        gestor.registrarTarea(5, "Entrega atrasada", "Demostrar prioridad por fecha",
                hoy.minusDays(1), 1, 15, calculo);
        System.out.println("IDs registrados: 1, 2, 3, 4, 5");

        System.out.println("\n2. Pendientes en orden de prioridad:");
        mostrar(gestor.obtenerPendientes());
        System.out.println("Orden esperado de IDs: 5, 2, 4, 1, 3");
        comprobarOrden(gestor.obtenerPendientes(), new int[]{5, 2, 4, 1, 3});
        System.out.println("Historial inicial: " + gestor.obtenerHistorial().size() + " tareas.");

        System.out.println("\n3. Consultar no elimina la tarea:");
        System.out.println(gestor.consultarSiguiente());
        System.out.println("Siguen pendientes: " + gestor.obtenerPendientes().size());

        System.out.println("\n4. Completar la siguiente:");
        Tarea completada = gestor.completarSiguiente();
        System.out.println("Completada: " + completada.getTitulo() + " | " + completada.getEstado());
        System.out.println("Nueva siguiente: " + gestor.consultarSiguiente().getTitulo());
        System.out.println("Historial:");
        mostrar(gestor.obtenerHistorial());

        System.out.println("\n5. Completar las tareas restantes:");
        while (gestor.consultarSiguiente() != null) {
            Tarea t = gestor.completarSiguiente();
            System.out.println("Atendida: ID " + t.getId() + " - " + t.getTitulo());
        }
        System.out.println("Consultar cola vacía: " + gestor.consultarSiguiente());
        System.out.println("Completar cola vacía: " + gestor.completarSiguiente());
        System.out.println("Historial final: " + gestor.obtenerHistorial().size() + " tareas.");
        comprobarOrden(gestor.obtenerHistorial(), new int[]{5, 2, 4, 1, 3});

        System.out.println("\n6. Validación del peso:");
        try {
            gestor.registrarTarea(6, "Tarea inválida", "Peso fuera de rango", hoy, 120, 120, calculo);
            throw new IllegalStateException("La validación del peso no funcionó.");
        } catch (EFueraRango e) {
            System.out.println("Se rechazó correctamente el peso 120 %: " + e.getMessage());
        }
        System.out.println("\nDemostración finalizada correctamente.");
    }

    private static void mostrar(List<Tarea> tareas) {
        if (tareas.isEmpty()) System.out.println("(Sin tareas)");
        for (Tarea t : tareas)
            System.out.println("ID " + t.getId() + " | " + t + " | " + t.getEstado());
    }

    private static void comprobarOrden(List<Tarea> tareas, int[] esperado) {
        if (tareas.size() != esperado.length)
            throw new IllegalStateException("La cantidad de tareas no coincide con la esperada.");
        for (int i = 0; i < esperado.length; i++) {
            if (tareas.get(i).getId() != esperado[i])
                throw new IllegalStateException("Orden incorrecto en la posición " + i);
        }
    }
}

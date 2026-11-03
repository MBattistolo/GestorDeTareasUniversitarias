import Exceptions.EFueraRango;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class MainSimulacion {

    public static void main(String[] args) throws EFueraRango {

        ArrayList<EntradaCola> tareas = new ArrayList<>();
        Random rnd = new Random(67);

        Asignatura asignatura = new Asignatura("EDA", "Estructuras de Datos");

        // Crear las 20 tareas
        for (int i = 0; i < 20; i++) {

            LocalDate fecha = LocalDate.of(2026, 11, 1)
                    .plusDays(rnd.nextInt(30));

            double peso = rnd.nextInt(100) + 1;

            Tarea tarea = new Tarea(i + 1,"Tarea " + (i + 1),"Prueba",fecha,peso,60,asignatura
            );

            tareas.add(new EntradaCola(tarea, i));
        }

        // Crear las simulaciones
        Simulacion cola = new Simulacion("PriorityQueue");
        Simulacion ordenada = new Simulacion("Lista ordenada");
        Simulacion desordenada = new Simulacion("Lista desordenada");

        // Medir los tiempos
        cola.medirTiempo(tareas);
        ordenada.medirTiempo(tareas);
        desordenada.medirTiempo(tareas);

        // Mostrar resultados
        System.out.println("===== SIMULACION =====");
        System.out.println("Tareas: 20");
        System.out.println("Repeticiones: 10000");

        System.out.println(cola);
        System.out.println(ordenada);
        System.out.println(desordenada);
    }
}
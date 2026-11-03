import java.time.LocalDate;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class Simulacion {

    private static final int CANTIDAD = 20;
    private static final int REPETICIONES = 10000;

    private String estructura;
    private double tiempoMs;

    public Simulacion(String estructura) {
        this.estructura = estructura;
        tiempoMs = 0;
    }

    public void medirTiempo(ArrayList<EntradaCola> tareas) {

        long inicio = System.nanoTime();

        for (int r = 0; r < REPETICIONES; r++) {

            // Probar PriorityQueue
            if (estructura.equals("PriorityQueue")) {

                PriorityQueue<EntradaCola> cola = new PriorityQueue<>();

                // Agregar tareas
                for (int i = 0; i < tareas.size(); i++) {
                    cola.offer(tareas.get(i));
                }

                // Sacar tareas
                while (!cola.isEmpty()) {
                    cola.poll();
                }

            // Probar ArrayList sin ordenar
            } else if (estructura.equals("Lista desordenada")) {

                ArrayList<EntradaCola> lista = new ArrayList<>();

                // Agregar tareas
                for (int i = 0; i < tareas.size(); i++) {
                    lista.add(tareas.get(i));
                }

                // Buscar y sacar la más prioritaria
                while (!lista.isEmpty()) {

                    int mejor = 0;

                    for (int i = 1; i < lista.size(); i++) {
                        if (lista.get(i).compareTo(lista.get(mejor)) < 0) {
                            mejor = i;
                        }
                    }

                    lista.remove(mejor);
                }

            // Probar ArrayList ordenado
            } else if (estructura.equals("Lista ordenada")) {

                ArrayList<EntradaCola> lista = new ArrayList<>();

                // Agregar tareas en orden
                for (int i = 0; i < tareas.size(); i++) {

                    EntradaCola entrada = tareas.get(i);
                    int posicion = 0;

                    while (posicion < lista.size()
                            && lista.get(posicion).compareTo(entrada) < 0) {
                        posicion++;
                    }

                    lista.add(posicion, entrada);
                }

                // Sacar tareas
                while (!lista.isEmpty()) {
                    lista.remove(0);
                }
            }
        }

        tiempoMs = (System.nanoTime() - inicio)
                / 1000000.0 / REPETICIONES;
    }

    public String getEstructura() {
        return estructura;
    }

    public double getTiempoMs() {
        return tiempoMs;
    }

    @Override
    public String toString() {
        return estructura + ": " + tiempoMs + " ms";
    }
}


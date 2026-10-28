import Exceptions.EEntradaInvalida;

import java.util.ArrayList;
import java.util.List;

public final class ColaPrioridad {
    private final List<EntradaCola> heap;

    public ColaPrioridad() {
        heap = new ArrayList<>();
    }

    private ColaPrioridad(List<EntradaCola> entradas) {
        heap = new ArrayList<>(entradas);
    }

    public void encolar(EntradaCola e) throws EEntradaInvalida {
        if (e == null)
            throw new EEntradaInvalida("La entrada es nula.");

        heap.add(e);
        subir(heap.size() - 1);
    }

    public EntradaCola desencolar() {
        if (estaVacia())
            return null;

        EntradaCola primero = heap.getFirst();
        EntradaCola ultimo = heap.removeLast();

        if (!estaVacia()) {
            heap.set(0, ultimo);
            bajar(0);
        }

        return primero;
    }

    public EntradaCola verPrimero() {
        return estaVacia() ? null : heap.getFirst();
    }

    public boolean estaVacia() {
        return heap.isEmpty();
    }

    public int tamano() {
        return heap.size();
    }

    public List<EntradaCola> listarEnOrden() {
        ColaPrioridad copia = new ColaPrioridad(heap);
        List<EntradaCola> resultado = new ArrayList<>();

        while (!copia.estaVacia())
            resultado.add(copia.desencolar());

        return resultado;
    }

    private boolean tieneMayorPrioridad(EntradaCola a, EntradaCola b) {
        Tarea ta = a.getTarea();
        Tarea tb = b.getTarea();

        int fecha = ta.getFechaEntrega().compareTo(tb.getFechaEntrega());
        if (fecha != 0)
            return fecha < 0;

        int peso = Double.compare(ta.getPesoNota(), tb.getPesoNota());
        if (peso != 0)
            return peso > 0;

        return a.getOrdenLlegada() < b.getOrdenLlegada();
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;

            if (!tieneMayorPrioridad(heap.get(i), heap.get(padre)))
                break;

            i = intercambiar(i, padre);
        }
    }

    private void bajar(int i) {
        while (2 * i + 1 < heap.size()) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int mejor = izq;

            if (der < heap.size() && tieneMayorPrioridad(heap.get(der), heap.get(izq)))
                mejor = der;

            if (!tieneMayorPrioridad(heap.get(mejor), heap.get(i)))
                i = intercambiar(i, mejor);
        }
    }

    private int intercambiar(int a, int b) {
        EntradaCola temporal = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, temporal);

        return b;
    }
}

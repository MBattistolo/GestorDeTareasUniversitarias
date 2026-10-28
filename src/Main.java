import javax.swing.SwingUtilities;

/** Inicia la aplicación gráfica. Responsable: Matías. */
public class Main {
    public static void main(String[] args) {
        // Swing debe crearse y actualizarse en su hilo de eventos.
        SwingUtilities.invokeLater(() -> {
            GestorTareas gestor = new GestorTareas();
            InterfazGestor interfaz = new InterfazGestor(gestor);
            interfaz.setVisible(true);
        });
    }
}

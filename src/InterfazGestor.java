import Exceptions.EFueraRango;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Interfaz gráfica; todas las operaciones de tareas pasan por GestorTareas. */
public class InterfazGestor extends JFrame {
    private final GestorTareas gestor;
    private int siguienteId;
    private final JTextField titulo = new JTextField(20);
    private final JTextField codigo = new JTextField("ED", 20);
    private final JTextField asignatura = new JTextField("Estructura de Datos", 20);
    private final JTextField fecha = new JTextField(LocalDate.now().plusDays(1).toString(), 20);
    private final JTextField peso = new JTextField("10", 20);
    private final JTextArea descripcion = new JTextArea(3, 20);
    private final DefaultTableModel pendientes = crearModelo();
    private final DefaultTableModel historial = crearModelo();
    private final JLabel resumen = new JLabel(" ");

    public InterfazGestor(GestorTareas gestor) {
        super("Gestor de tareas universitarias");
        this.gestor = java.util.Objects.requireNonNull(gestor);
        siguienteId = 1;
        for (Tarea t : gestor.obtenerPendientes()) siguienteId = Math.max(siguienteId, t.getId() + 1);
        for (Tarea t : gestor.obtenerHistorial()) siguienteId = Math.max(siguienteId, t.getId() + 1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        contenido.add(new JLabel("Prioridad: fecha anterior → mayor peso → orden de registro"), BorderLayout.NORTH);
        contenido.add(crearFormulario(), BorderLayout.WEST);
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Pendientes por prioridad", crearTabla(pendientes));
        pestanas.addTab("Historial de completadas", crearTabla(historial));
        contenido.add(pestanas, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(8, 8));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boton(acciones, "Consultar siguiente", this::mostrarSiguiente);
        boton(acciones, "Completar siguiente", this::completarSiguiente);
        boton(acciones, "Cargar ejemplos", this::cargarEjemplos);
        inferior.add(acciones, BorderLayout.NORTH);
        inferior.add(resumen, BorderLayout.SOUTH);
        contenido.add(inferior, BorderLayout.SOUTH);
        setContentPane(contenido);
        setMinimumSize(new Dimension(1050, 580));
        setSize(1250, 680);
        setLocationRelativeTo(null);
        actualizar();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nueva tarea"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(3, 8, 3, 8);
        int fila = 0;
        String[] etiquetas = {"Título", "Código de asignatura", "Nombre de asignatura",
                "Entrega (AAAA-MM-DD)", "Peso en la nota (0 a 100 %)"};
        JTextField[] campos = {titulo, codigo, asignatura, fecha, peso};
        for (int i = 0; i < campos.length; i++) {
            c.gridy = fila++;
            panel.add(new JLabel(etiquetas[i]), c);
            c.gridy = fila++;
            panel.add(campos[i], c);
        }
        c.gridy = fila++;
        panel.add(new JLabel("Descripción (opcional)"), c);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        c.gridy = fila++;
        panel.add(new JScrollPane(descripcion), c);
        JButton registrar = new JButton("Registrar tarea");
        registrar.addActionListener(e -> registrarDesdeFormulario());
        c.gridy = fila++;
        panel.add(registrar, c);
        c.gridy = fila;
        c.weighty = 1;
        panel.add(new JPanel(), c);
        return panel;
    }

    private static DefaultTableModel crearModelo() {
        return new DefaultTableModel(new String[]{"ID", "Tarea", "Asignatura", "Entrega", "Peso (%)", "Estado"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }

    private JScrollPane crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFillsViewportHeight(true);
        // Sin ordenación manual: las filas deben reflejar el orden de prioridad.
        return new JScrollPane(tabla);
    }

    private void boton(JPanel panel, String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        panel.add(boton);
    }

    private void registrarDesdeFormulario() {
        try {
            if (titulo.getText().trim().isEmpty() || codigo.getText().trim().isEmpty()
                    || asignatura.getText().trim().isEmpty())
                throw new IllegalArgumentException("Completa el título, el código y la asignatura.");

            LocalDate entrega = LocalDate.parse(fecha.getText().trim());
            double porcentaje = Double.parseDouble(peso.getText().trim().replace(',', '.'));
            if (!Double.isFinite(porcentaje))
                throw new IllegalArgumentException("El peso debe ser un número finito entre 0 y 100.");

            gestor.registrarTarea(siguienteId, titulo.getText().trim(), descripcion.getText().trim(),
                    entrega, porcentaje, new Asignatura(codigo.getText().trim(), asignatura.getText().trim()));
            siguienteId++;
            titulo.setText("");
            descripcion.setText("");
            actualizar();
            titulo.requestFocusInWindow();
        } catch (DateTimeParseException e) {
            error("Escribe una fecha válida como 2026-11-05 (AAAA-MM-DD).");
        } catch (NumberFormatException e) {
            error("El peso debe ser numérico. Ejemplo: 25 o 12,5.");
        } catch (EFueraRango e) {
            error(e.getMessage());
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void mostrarSiguiente() {
        Tarea t = gestor.consultarSiguiente();
        JOptionPane.showMessageDialog(this, t == null ? "No hay tareas pendientes." : t.toString(),
                "Siguiente tarea", JOptionPane.INFORMATION_MESSAGE);
    }

    private void completarSiguiente() {
        Tarea t = gestor.consultarSiguiente();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "No hay tareas pendientes.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Completaste la siguiente tarea?\n" + t,
                "Completar siguiente", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            gestor.completarSiguiente();
            actualizar();
        }
    }

    private void actualizar() {
        llenar(pendientes, gestor.obtenerPendientes());
        llenar(historial, gestor.obtenerHistorial());
        resumen.setText("Pendientes: " + pendientes.getRowCount() + " | Completadas: "
                + historial.getRowCount() + " | Datos en memoria: se pierden al cerrar.");
    }

    private void llenar(DefaultTableModel modelo, List<Tarea> tareas) {
        modelo.setRowCount(0);
        for (Tarea t : tareas)
            modelo.addRow(new Object[]{t.getId(), t.getTitulo(), t.getAsignatura().getNombre(),
                    t.getFechaEntrega(), t.getPesoNota(), t.getEstado()});
    }

    private void cargarEjemplos() {
        try {
            Asignatura ed = new Asignatura("ED", "Estructura de Datos");
            Asignatura cal = new Asignatura("CAL", "Cálculo");
            LocalDate hoy = LocalDate.now();
            ejemplo("Proyecto de estructuras", hoy.plusDays(3), 30, ed);
            ejemplo("Taller de programación", hoy.plusDays(1), 10, ed);
            ejemplo("Cuestionario de cálculo", hoy.plusDays(3), 5, cal);
            ejemplo("Ejercicio adicional", hoy.plusDays(1), 10, ed);
            ejemplo("Entrega atrasada", hoy.minusDays(1), 1, cal);
            actualizar();
        } catch (EFueraRango e) {
            error(e.getMessage());
        }
    }

    private void ejemplo(String nombre, LocalDate entrega, double porcentaje, Asignatura materia)
            throws EFueraRango {
        gestor.registrarTarea(siguienteId, nombre, "Ejemplo para la exposición", entrega, porcentaje, materia);
        siguienteId++;
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisa los datos", JOptionPane.ERROR_MESSAGE);
    }
}

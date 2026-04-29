/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.formularioswing;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class FormularioSwing extends JFrame {

    // Componentes del formulario
    private JTextField txtNombre, txtApellido, txtDni, txtPasaporte, txtTelefono, txtCP, txtDomicilio;
    private JButton btnEnviar;

    public FormularioSwing() {
        setTitle("Carga de contacto");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Inicialización y Validaciones de Entrada (Dentro de campo) ---
        
        txtNombre = crearCampoAlfabetico(20);
        txtApellido = crearCampoAlfabetico(20);
        txtDni = crearCampoNumerico(8);
        txtPasaporte = crearCampoLimitado(9); // 1 letra + 8 números
        txtTelefono = crearCampoTelefono();
        txtCP = crearCampoNumerico(4);
        txtDomicilio = crearCampoLimitado(50);

        // --- Armado de la Interfaz ---
        agregarFila(0, "Nombre:", txtNombre, gbc);
        agregarFila(1, "Apellido:", txtApellido, gbc);
        agregarFila(2, "DNI:", txtDni, gbc);
        agregarFila(3, "Pasaporte:", txtPasaporte, gbc);
        agregarFila(4, "Teléfono:", txtTelefono, gbc);
        agregarFila(5, "Cód. Postal:", txtCP, gbc);
        agregarFila(6, "Domicilio:", txtDomicilio, gbc);

        btnEnviar = new JButton("Guardar Contacto");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        add(btnEnviar, gbc);

        // --- Lógica de Validación Exclusiva (DNI o Pasaporte) ---
        configurarExclusividad();

        // --- Validación Final (Fuera de campo) ---
        btnEnviar.addActionListener(e -> validarFormulario());
    }

    // Método para agregar etiquetas y campos ordenados
    private void agregarFila(int fila, String etiqueta, JTextField campo, GridBagConstraints gbc) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = fila;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    // Filtro: Solo letras y límite de caracteres
    private JTextField crearCampoAlfabetico(int limite) {
        JTextField campo = new JTextField(20);
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*") && (fb.getDocument().getLength() + text.length() - length) <= limite) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return campo;
    }

    // Filtro: Solo números y límite
    private JTextField crearCampoNumerico(int limite) {
        JTextField campo = new JTextField(20);
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]*") && (fb.getDocument().getLength() + text.length() - length) <= limite) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return campo;
    }

    // Filtro: Teléfono (Números y símbolos permitidos)
    private JTextField crearCampoTelefono() {
        JTextField campo = new JTextField(20);
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9+\\-() ]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return campo;
    }

    // Filtro: Límite general de caracteres
    private JTextField crearCampoLimitado(int limite) {
        JTextField campo = new JTextField(20);
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= limite) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return campo;
    }

    // Lógica: Si escribe en DNI, bloquea Pasaporte y viceversa
    private void configurarExclusividad() {
        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                txtPasaporte.setEnabled(txtDni.getText().isEmpty());
                txtDni.setEnabled(txtPasaporte.getText().isEmpty());
            }
        };
        txtDni.addKeyListener(adapter);
        txtPasaporte.addKeyListener(adapter);
    }

    private void validarFormulario() {
        try {
            // Validar DNI si tiene contenido
            if (!txtDni.getText().isEmpty()) {
                long dni = Long.parseLong(txtDni.getText());
                if (dni < 10000000 || dni > 60000000) throw new Exception("DNI fuera de rango (10M - 60M)");
            }
            
            // Validar Pasaporte si tiene contenido
            if (!txtPasaporte.getText().isEmpty()) {
                String p = txtPasaporte.getText();
                if (!p.matches("[A-Z][0-9]{8}")) throw new Exception("Pasaporte Inválido (Ej: N39392288)");
                long numP = Long.parseLong(p.substring(1));
                if (numP < 10000000 || numP > 60000000) throw new Exception("Número de pasaporte fuera de rango");
            }

            // Validar Teléfono
            if (txtTelefono.getText().replaceAll("[^0-9]", "").length() <= 6) {
                throw new Exception("Teléfono debe tener más de 6 dígitos numéricos");
            }

            // Validar CP
            if (txtCP.getText().length() != 4) throw new Exception("Código Postal debe ser de 4 dígitos");

            // Si todo está bien
            JOptionPane.showMessageDialog(this, "¡Formulario cargado con éxito!");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioSwing().setVisible(true));
    }
}
package com.mycompany.practicabaseswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ejemplo de JOptionPane y Conversión de Tipos.
 * Basado en: "Apunte sobre Validación en formularios"
 */
public class FormularioValidado extends JFrame {
    private JTextField txtEdad;
    private JButton btnValidar;

    public FormularioValidado() {
        super("Validación de Datos");
        setLayout(new FlowLayout());
        
        add(new JLabel("Ingrese su edad:"));
        txtEdad = new JTextField(10);
        add(txtEdad);
        
        btnValidar = new JButton("Validar y Guardar");
        add(btnValidar);

        btnValidar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validarEntrada();
            }
        });

        setSize(350, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void validarEntrada() {
        try {
            // Conversión de String a Integer (explicado en el apunte)
            int edad = Integer.parseInt(txtEdad.getText());
            
            if (edad >= 18) {
                JOptionPane.showMessageDialog(this, "Datos guardados con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Debe ser mayor de 18 años", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            // MEJORA: Manejo de error si el usuario no ingresa un número
            JOptionPane.showMessageDialog(this, "Error: Ingrese un número válido", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new FormularioValidado().setVisible(true);
    }
}
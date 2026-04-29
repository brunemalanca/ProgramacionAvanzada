package com.mycompany.practicabaseswing;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Ejemplo de KeyListener y evt.consume().
 * Basado en: "Apunte sobre Validación - Eventos de Teclado"
 */
public class FiltroTeclado extends JFrame {
    private JTextField txtSoloNumeros;

    public FiltroTeclado() {
        super("Filtro de Teclado");
        setLayout(new FlowLayout());
        
        txtSoloNumeros = new JTextField(15);
        
        // MEJORA: Validación en tiempo real (solo permite números)
        txtSoloNumeros.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Ignora el caracter si no es número
                    Toolkit.getDefaultToolkit().beep(); // Sonido de alerta
                }
            }
        });

        add(new JLabel("Solo permite números:"));
        add(txtSoloNumeros);
        
        setSize(300, 120);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        new FiltroTeclado().setVisible(true);
    }
}
package com.mycompany.practicabaseswing;

import javax.swing.JOptionPane;

public class PracticaBaseSwing {

    public static void main(String[] args) {
        // MEJORA: Un pequeño menú para elegir qué ejemplo ejecutar
        String[] opciones = {
            "1. Ventana Principal (Herencia)", 
            "2. Formulario con Validación", 
            "3. Filtro de Teclado", 
            "4. Contador (MVC)", 
            "Salir"
        };

        while (true) {
            String eleccion = (String) JOptionPane.showInputDialog(null, 
                    "Seleccione el ejemplo que desea revisar:", 
                    "TP Swing - Menú Principal", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, opciones, opciones[0]);

            if (eleccion == null || eleccion.equals("Salir")) break;

            switch (eleccion) {
                case "1. Ventana Principal (Herencia)":
                    new MiVentanaMejorada().setVisible(true);
                    break;
                case "2. Formulario con Validación":
                    new FormularioValidado().setVisible(true);
                    break;
                case "3. Filtro de Teclado":
                    new FiltroTeclado().setVisible(true);
                    break;
                case "4. Contador (MVC)":
                    new ContadorMVC().setVisible(true);
                    break;
            }
        }
    }
}
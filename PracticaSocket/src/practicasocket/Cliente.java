package practicasocket;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    // Dirección del servidor al que se va a conectar (en este caso, la misma PC)
    private static final String HOST = "localhost";
    // Puerto que debe coincidir exactamente con el que usa el Servidor
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE INICIADO ===");
        System.out.println("Conectando al servidor " + HOST + ":" + PUERTO + "...");

        try (
            // Crea la conexión con el servidor en el puerto 5000
            Socket socket = new Socket(HOST, PUERTO);

            // entrada: para leer los mensajes que llegan del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // salida: para enviar mensajes al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // teclado: para leer lo que escribe el usuario en la consola
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!\n");

            // Lee y muestra los mensajes de bienvenida que manda el servidor al conectarse
            String lineaBienvenida;
            while ((lineaBienvenida = entrada.readLine()) != null) {
                System.out.println("[SERVIDOR]: " + lineaBienvenida);

                // El servidor manda "---..." como última línea del saludo, ahí paramos
                if (lineaBienvenida.startsWith("---")) {
                    break;
                }
            }

            // Bucle principal: el usuario escribe, el servidor responde, y así sucesivamente
            while (true) {
                System.out.print("Vos: ");

                // Espera a que el usuario escriba algo y presione Enter
                String mensaje = teclado.nextLine();

                // Si el usuario presionó Enter sin escribir nada, ignora y vuelve a pedir
                if (mensaje.trim().isEmpty()) {
                    continue;
                }

                // Envía el mensaje al servidor
                salida.println(mensaje);

                // Lee la respuesta del servidor y la muestra
                String respuesta = entrada.readLine();
                System.out.println("[SERVIDOR]: " + respuesta);

                // Si el usuario mandó SALIR, cierra el programa
                if (mensaje.equalsIgnoreCase("SALIR")) {
                    System.out.println("[CLIENTE] Conexión cerrada. ¡Hasta luego!");
                    break;
                }
            }

        } catch (ConnectException e) {
            // Ocurre cuando el servidor no está corriendo todavía
            System.err.println("[CLIENTE] No se pudo conectar al servidor. ¿Está corriendo el Servidor.java?");
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error de I/O: " + e.getMessage());
        }
    }
}
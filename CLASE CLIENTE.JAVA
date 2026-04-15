
package practicasocket;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE INICIADO ===");
        System.out.println("Conectando al servidor " + HOST + ":" + PUERTO + "...");

        try (
            Socket socket = new Socket(HOST, PUERTO);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!\n");

            // Hilo separado para leer mensajes del servidor sin bloquear el input del usuario
            Thread lectorServidor = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("[SERVIDOR]: " + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENTE] Conexión con el servidor cerrada.");
                }
            });

            lectorServidor.setDaemon(true);
            lectorServidor.start();

            // Pequeña pausa para que el servidor muestre el mensaje de bienvenida antes del prompt
            Thread.sleep(300);

            // Bucle principal: el usuario escribe mensajes
            while (true) {
                System.out.print("Vos: ");
                String mensaje = teclado.nextLine();

                if (mensaje.trim().isEmpty()) {
                    continue;
                }

                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("SALIR")) {
                    Thread.sleep(300); // espera a recibir el mensaje de despedida
                    System.out.println("[CLIENTE] Conexión cerrada. ¡Hasta luego!");
                    break;
                }

                // Pequeña pausa para que la respuesta llegue antes del próximo prompt
                Thread.sleep(200);
            }

        } catch (ConnectException e) {
            System.err.println("[CLIENTE] No se pudo conectar al servidor. ¿Está corriendo el Servidor.java?");
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error de I/O: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

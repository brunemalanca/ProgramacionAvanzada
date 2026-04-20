import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private static final int PUERTO = 5000;
    private static final Map<String, ManejadorCliente> clientesConectados = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("[LOG] === SERVIDOR MULTIHILO INICIADO EN PUERTO " + PUERTO + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept(); 
                new Thread(new ManejadorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[LOG] Error crítico: " + e.getMessage());
        }
    }

    static class ManejadorCliente implements Runnable {
        private final Socket socket;
        private PrintWriter salida;
        private String nombreUsuario;

        ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.salida = out;
                
                salida.println("Ingresá tu nombre de usuario:");
                String nombreElegido = entrada.readLine();
                if (nombreElegido == null) return;
                
                this.nombreUsuario = validarNombre(nombreElegido.trim());
                clientesConectados.put(this.nombreUsuario, this);
                
                System.out.println("[LOG] [+] " + nombreUsuario + " se ha unido.");
                enviarMenu();

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println("[LOG] [" + nombreUsuario + "]: " + mensaje);
                    String msgUpper = mensaje.toUpperCase();

                    if (msgUpper.equalsIgnoreCase("SALIR")) {
                        responder("Desconectando...");
                        break;
                    } 
                    else if (msgUpper.equals("HORA")) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        responder("Fecha/Hora actual: " + dtf.format(LocalDateTime.now()));
                    }
                    else if (msgUpper.equals("LISTA")) {
                        responder("Usuarios conectados: " + clientesConectados.keySet());
                    }
                    else if (msgUpper.startsWith("CONSONANTES ")) {
                        String palabra = mensaje.substring(12).trim();
                        responder(obtenerConsonantes(palabra));
                    }
                    else if (mensaje.startsWith("*")) {
                        // AQUÍ LLAMAMOS AL MÉTODO QUE ARREGLAMOS
                        procesarMensajeEspecial(mensaje);
                    }
                    else {
                        responder("[ERROR] Comando no reconocido o formato incorrecto.");
                    }
                }
            } catch (IOException e) {
                System.err.println("[LOG] Error con " + nombreUsuario + ": " + e.getMessage());
            } finally {
                if (nombreUsuario != null) {
                    clientesConectados.remove(nombreUsuario);
                    System.out.println("[LOG] [-] " + nombreUsuario + " se ha ido.");
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        // Método auxiliar para responder y loguear al mismo tiempo
        private void responder(String texto) {
            salida.println(texto);
            System.out.println("[SERVIDOR para " + nombreUsuario + "]: " + texto);
        }

        private String validarNombre(String nombreBase) {
            String resultado = nombreBase;
            int i = 1;
            while (clientesConectados.containsKey(resultado)) {
                resultado = nombreBase + i;
                i++;
            }
            if (!resultado.equals(nombreBase)) {
                salida.println("[AVISO] El nombre ya existía. Se te asignó: " + resultado);
            }
            return resultado;
        }

        private void enviarMenu() {
            salida.println("\n--- BIENVENIDO " + nombreUsuario + " ---");
            salida.println("COMANDOS:");
            salida.println("  *ALL <msg>         -> Enviar a todos");
            salida.println("  *User1,User2 <msg> -> Enviar a específicos");
            salida.println("  CONSONANTES <word> -> Analizar letras");
            salida.println("  HORA                -> Ver fecha y hora");
            salida.println("  LISTA               -> Ver conectados");
            salida.println("  SALIR               -> Salir del chat");
            salida.println("------------------------------------------");
        }

        private void procesarMensajeEspecial(String input) {
            try {
                int primerEspacio = input.indexOf(" ");
                if (primerEspacio == -1) {
                    responder("[ERROR] Formato incorrecto. Usa *User Mensaje");
                    return;
                }

                String cabecera = input.substring(1, primerEspacio).trim();
                String contenido = input.substring(primerEspacio).trim();

                if (cabecera.equalsIgnoreCase("ALL")) {
                    clientesConectados.forEach((nombre, manejador) -> {
                        if (!nombre.equals(this.nombreUsuario)) {
                            manejador.salida.println("[TODOS] " + this.nombreUsuario + ": " + contenido);
                        }
                    });
                } else {
                    String[] destinatarios = cabecera.split(",");
                    for (String d : destinatarios) {
                        String dLimpio = d.trim();
                        if (dLimpio.startsWith("*")) dLimpio = dLimpio.substring(1);

                        // BÚSQUEDA INSENSIBLE A MAYÚSCULAS
                        ManejadorCliente receptor = null;
                        for (String nombreConectado : clientesConectados.keySet()) {
                            if (nombreConectado.equalsIgnoreCase(dLimpio)) {
                                receptor = clientesConectados.get(nombreConectado);
                                break;
                            }
                        }

                        if (receptor != null) {
                            receptor.salida.println("[PRIVADO de " + this.nombreUsuario + "]: " + contenido);
                            System.out.println("[LOG-PRIVADO] " + this.nombreUsuario + " para " + dLimpio + ": " + contenido);
                        } else {
                            responder("[ERROR] El usuario '" + dLimpio + "' no existe.");
                        }
                    }
                }
            } catch (Exception e) {
                responder("[ERROR] Error al procesar el mensaje.");
            }
        }

        private String obtenerConsonantes(String palabra) {
            String vocales = "aeiouáéíóúüAEIOUÁÉÍÓÚÜ";
            StringBuilder todas = new StringBuilder();
            LinkedHashSet<Character> unicas = new LinkedHashSet<>();
            int cant = 0;
            for (char c : palabra.toCharArray()) {
                if (Character.isLetter(c) && vocales.indexOf(c) == -1) {
                    todas.append(c).append(" ");
                    unicas.add(Character.toLowerCase(c));
                    cant++;
                }
            }
            return "Palabra: " + palabra + " | Consonantes: " + todas + " | Únicas: " + unicas + " | Total: " + cant;
        }
    }
}
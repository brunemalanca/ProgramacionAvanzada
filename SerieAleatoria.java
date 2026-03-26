import java.util.Random;

public class MyClass {
    public static void main(String[] args) {
        Random random = new Random();
        int cantidad = 500;
        long suma = 0;

        for (int i = 0; i < cantidad; i++) {
            int numero = random.nextInt(991) + 10;
            suma += numero;
        }

        double promedio = (double) suma/cantidad;

        System.out.println("----------------------------");
        System.out.println(" Cantidad de numeros: " + cantidad);
        System.out.println(" Total suma:          " + suma);
        System.out.printf(" Promedio:            %.2f%n", promedio); //ver el promedio sólo con dos decimales
        System.out.printf("-----------------------------");
    }
}

package cl.proyecto.morfologia;

import cl.proyecto.morfologia.menu.MenuRunner;

/**
 * Clase principal del proyecto.
 * Punto de entrada de la aplicación.
 * 
 * Permite ejecutar la aplicación ya sea con parámetros de línea de comandos
 * o de forma interactiva a través de un menú por consola.
 */
public class Main {
    public static void main(String[] args) {
        // Llama al menú principal encargado de interpretar parámetros o abrir el menú interactivo
        MenuRunner.run(args);
    }
}

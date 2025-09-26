package cl.proyecto.morfologia.model;

/**
 * Enum que representa la operación de morfología:
 * - Erosión
 * - Dilatación
 */
public enum Operation {
    EROSION, DILATACION;

    public static Operation from(String s) {
        return "dilatacion".equalsIgnoreCase(s) ? DILATACION : EROSION;
    }
}

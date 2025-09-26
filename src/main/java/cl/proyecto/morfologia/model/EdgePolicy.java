package cl.proyecto.morfologia.model;

/**
 * Enum que define cómo tratar los bordes de la imagen:
 * - IGNORE: ignora píxeles fuera de rango
 * - PAD: rellena con valores neutros
 */
public enum EdgePolicy {
    IGNORE, PAD;

    public static EdgePolicy from(String s) {
        return "pad".equalsIgnoreCase(s) ? PAD : IGNORE;
    }
}

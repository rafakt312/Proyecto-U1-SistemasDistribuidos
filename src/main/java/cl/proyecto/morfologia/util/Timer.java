package cl.proyecto.morfologia.util;

/**
 * Clase simple para medir tiempo de ejecución.
 */
public final class Timer {
    private long t0;
    public void start(){ t0 = System.nanoTime(); }
    public double ms(){ return (System.nanoTime() - t0)/1e6; }
}

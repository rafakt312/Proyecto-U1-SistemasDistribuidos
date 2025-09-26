package cl.proyecto.morfologia.core;

import cl.proyecto.morfologia.model.EdgePolicy;
import cl.proyecto.morfologia.model.Operation;
import cl.proyecto.morfologia.model.StructuringElement;
import cl.proyecto.morfologia.util.ImageIOUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Implementación paralela de las operaciones de morfología matemática
 * (Erosión y Dilatación).
 *
 * - Divide la imagen en "tiles" horizontales (bandas de filas).
 * - Cada tile incluye un "halo" o solapamiento adicional para que los bordes
 *   de los bloques tengan acceso a los píxeles vecinos necesarios.
 * - Se ejecuta con un pool fijo de hilos.
 */
public final class MorphologyParallel {

    /**
     * Aplica erosión o dilatación en paralelo a una imagen completa.
     *
     * @param img Imagen de entrada
     * @param op Operación (EROSION o DILATACION)
     * @param se Elemento estructurante
     * @param edgePolicy Política de borde (IGNORE o PAD)
     * @param threads Número de hilos a utilizar
     * @return Imagen resultante procesada
     */
    public static BufferedImage apply(BufferedImage img, Operation op, boolean[][] se, EdgePolicy edgePolicy, int threads)
            throws InterruptedException, ExecutionException {
        int w = img.getWidth(), h = img.getHeight();

        // Extrae canales RGB de la imagen
        int[][] R = new int[h][w], G = new int[h][w], B = new int[h][w];
        ImageIOUtils.splitRGB(img, R,G,B);

        // Procesa cada canal en paralelo (pero secuencial por simplicidad)
        int[][] rOut = parallelChannel(R, op, se, edgePolicy, threads);
        int[][] gOut = parallelChannel(G, op, se, edgePolicy, threads);
        int[][] bOut = parallelChannel(B, op, se, edgePolicy, threads);

        // Reconstruye la imagen final
        return ImageIOUtils.mergeRGB(rOut, gOut, bOut);
    }

    /**
     * Procesa un único canal de la imagen en paralelo.
     */
    static int[][] parallelChannel(int[][] channel, Operation op, boolean[][] se, EdgePolicy edgePolicy, int threads)
            throws InterruptedException, ExecutionException {
        int h = channel.length, w = channel[0].length;
        int[][] out = new int[h][w];

        // Radios del elemento estructurante
        int rr = StructuringElement.radiusRow(se);
        int rc = StructuringElement.radiusCol(se);

        // Cálculo del número de tiles (bandas horizontales)
        int tiles = Math.min(threads, Math.max(1, h / Math.max(32, rr*2+1)));
        int rowsPerTile = (int)Math.ceil(h / (double)tiles);

        // Pool de hilos fijo
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        // Crea tareas para cada tile
        for (int t=0; t<tiles; t++){
            int y0 = t * rowsPerTile;               // fila inicial
            int y1 = Math.min(h, y0 + rowsPerTile); // fila final (no inclusiva)

            // Halo superior e inferior
            int haloTop = Math.max(0, y0 - rr);
            int haloBot = Math.min(h, y1 + rr);

            // Copia del sub-bloque con halo
            int[][] sub = new int[haloBot - haloTop][w];
            for (int sy=haloTop; sy<haloBot; sy++) {
                System.arraycopy(channel[sy], 0, sub[sy - haloTop], 0, w);
            }

            final int tileY0 = y0, tileY1 = y1, baseY = haloTop;

            // Tarea paralela
            futures.add(pool.submit(() -> {
                for (int y=tileY0; y<tileY1; y++){
                    for (int x=0; x<w; x++){
                        // Valor inicial: depende de la operación
                        int acc = (op==Operation.EROSION) ? 255 : 0;

                        // Recorre el elemento estructurante
                        for (int dy=-rr; dy<=rr; dy++){
                            int sy = y + dy - baseY; // coordenada dentro del sub-bloque
                            int gy = y + dy;         // coordenada global
                            if (gy < 0 || gy >= channel.length){
                                // Caso borde
                                if (edgePolicy == EdgePolicy.PAD) {
                                    int val = (op==Operation.EROSION) ? 255 : 0;
                                    for (int dx=-rc; dx<=rc; dx++){
                                        if (!se[dy+rr][dx+rc]) continue;
                                        acc = (op==Operation.EROSION) ? Math.min(acc, val) : Math.max(acc, val);
                                    }
                                }
                                continue;
                            }
                            for (int dx=-rc; dx<=rc; dx++){
                                if (!se[dy+rr][dx+rc]) continue;
                                int gx = x + dx;
                                if (gx < 0 || gx >= w){
                                    // Caso borde
                                    if (edgePolicy == EdgePolicy.PAD) {
                                        int val = (op==Operation.EROSION) ? 255 : 0;
                                        acc = (op==Operation.EROSION) ? Math.min(acc, val) : Math.max(acc, val);
                                    }
                                    continue;
                                }
                                // Valor real del canal
                                int v = sub[sy][gx];
                                acc = (op==Operation.EROSION) ? Math.min(acc, v) : Math.max(acc, v);
                            }
                        }
                        // Guarda el resultado en la salida global
                        out[y][x] = acc;
                    }
                }
            }));
        }

        // Espera a que terminen todos los hilos
        for (Future<?> f : futures) f.get();
        pool.shutdown();
        pool.awaitTermination(7, TimeUnit.DAYS);

        return out;
    }
}

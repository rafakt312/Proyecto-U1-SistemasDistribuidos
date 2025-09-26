package cl.proyecto.morfologia.core;

import cl.proyecto.morfologia.model.EdgePolicy;
import cl.proyecto.morfologia.model.Operation;
import cl.proyecto.morfologia.model.StructuringElement;
import cl.proyecto.morfologia.util.ImageIOUtils;

import java.awt.image.BufferedImage;

/**
 * Implementación secuencial de erosión y dilatación.
 */
public final class Morphology {

    public static BufferedImage apply(BufferedImage img, Operation op, boolean[][] se, EdgePolicy edgePolicy){
        int w = img.getWidth(), h = img.getHeight();
        int[][] R = new int[h][w], G = new int[h][w], B = new int[h][w];
        ImageIOUtils.splitRGB(img, R,G,B);

        int[][] rOut = applyToChannel(R, op, se, edgePolicy);
        int[][] gOut = applyToChannel(G, op, se, edgePolicy);
        int[][] bOut = applyToChannel(B, op, se, edgePolicy);

        return ImageIOUtils.mergeRGB(rOut, gOut, bOut);
    }

    public static int[][] applyToChannel(int[][] channel, Operation op, boolean[][] se, EdgePolicy edgePolicy){
        int h = channel.length, w = channel[0].length;
        int[][] out = new int[h][w];
        int rr = StructuringElement.radiusRow(se);
        int rc = StructuringElement.radiusCol(se);
        final boolean isErosion = (op == Operation.EROSION);

        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int acc = isErosion ? 255 : 0;
                for (int dy=-rr; dy<=rr; dy++){
                    int sy = y + dy;
                    if (sy < 0 || sy >= h){
                        if (edgePolicy == EdgePolicy.PAD) {
                            int val = isErosion ? 255 : 0;
                            for (int dx=-rc; dx<=rc; dx++){
                                if (!se[dy+rr][dx+rc]) continue;
                                acc = isErosion ? Math.min(acc, val) : Math.max(acc, val);
                            }
                        }
                        continue;
                    }
                    for (int dx=-rc; dx<=rc; dx++){
                        if (!se[dy+rr][dx+rc]) continue;
                        int sx = x + dx;
                        if (sx < 0 || sx >= w){
                            if (edgePolicy == EdgePolicy.PAD) {
                                int val = isErosion ? 255 : 0;
                                acc = isErosion ? Math.min(acc, val) : Math.max(acc, val);
                            }
                            continue;
                        }
                        int v = channel[sy][sx];
                        acc = isErosion ? Math.min(acc, v) : Math.max(acc, v);
                    }
                }
                out[y][x] = acc;
            }
        }
        return out;
    }
}

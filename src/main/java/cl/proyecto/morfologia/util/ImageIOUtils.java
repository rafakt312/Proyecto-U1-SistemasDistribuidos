package cl.proyecto.morfologia.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilidades para leer y escribir imágenes PNG y trabajar con canales RGB.
 */
public final class ImageIOUtils {
    public static BufferedImage readPng(File f) throws Exception {
        BufferedImage img = ImageIO.read(f);
        if (img == null) throw new IllegalArgumentException("No es una imagen válida PNG: " + f);
        return toARGB(img);
    }

    public static void writePng(BufferedImage img, File f) throws Exception {
        if (!ImageIO.write(img, "png", f)) throw new IllegalStateException("No se pudo escribir PNG: " + f);
    }

    // Convierte a formato ARGB para acceso más rápido.
    public static BufferedImage toARGB(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) return src;
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        dst.getGraphics().drawImage(src, 0, 0, null);
        return dst;
    }

    // Divide en canales R, G, B
    public static void splitRGB(BufferedImage img, int[][] R, int[][] G, int[][] B) {
        final int w = img.getWidth(), h = img.getHeight();
        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int argb = img.getRGB(x,y);
                R[y][x] = (argb>>16)&0xFF;
                G[y][x] = (argb>>8)&0xFF;
                B[y][x] = argb&0xFF;
            }
        }
    }

    // Une canales R, G, B en una imagen
    public static BufferedImage mergeRGB(int[][] R, int[][] G, int[][] B) {
        int h = R.length, w = R[0].length;
        BufferedImage out = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
        final int alpha = 0xFF<<24;
        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int r=R[y][x]&0xFF, g=G[y][x]&0xFF, b=B[y][x]&0xFF;
                out.setRGB(x,y, alpha | (r<<16) | (g<<8) | b);
            }
        }
        return out;
    }
}

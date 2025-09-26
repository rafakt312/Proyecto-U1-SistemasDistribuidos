package cl.proyecto.morfologia.model;

/**
 * Clase que define los elementos estructurantes.
 * Se representan como matrices booleanas.
 */
public final class StructuringElement {

    public static boolean[][] build(int id) {
        return switch (id) {
            case 1 -> square3();
            case 2 -> cross3();
            case 3 -> x3();
            case 4 -> hline3();
            case 5 -> diamond5();
            default -> throw new IllegalArgumentException("SE inválido (1..5)");
        };
    }

    public static String name(int id) {
        return switch (id) {
            case 1 -> "Cuadrado3x3";
            case 2 -> "Cruz3x3";
            case 3 -> "X3x3";
            case 4 -> "LineaH1x3";
            case 5 -> "Diamante5x5";
            default -> "SE?";
        };
    }

    private static boolean[][] square3() {
        boolean[][] m = new boolean[3][3];
        for (int i=0;i<3;i++) for (int j=0;j<3;j++) m[i][j]=true;
        return m;
    }
    private static boolean[][] cross3() {
        boolean[][] m = new boolean[3][3];
        m[0][1]=m[1][0]=m[1][1]=m[1][2]=m[2][1]=true;
        return m;
    }
    private static boolean[][] x3() {
        boolean[][] m = new boolean[3][3];
        m[0][0]=m[0][2]=m[1][1]=m[2][0]=m[2][2]=true;
        return m;
    }
    private static boolean[][] hline3() {
        boolean[][] m = new boolean[3][3];
        m[1][0]=m[1][1]=m[1][2]=true;
        return m;
    }
    private static boolean[][] diamond5() {
        boolean[][] m = new boolean[5][5];
        int c=2;
        for (int i=0;i<5;i++) {
            for (int j=0;j<5;j++) {
                m[i][j] = Math.abs(i-c) + Math.abs(j-c) <= 2;
            }
        }
        return m;
    }

    public static int radiusRow(boolean[][] se){ return se.length/2; }
    public static int radiusCol(boolean[][] se){ return se[0].length/2; }
}

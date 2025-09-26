package cl.proyecto.morfologia.menu;

import cl.proyecto.morfologia.core.Benchmark;
import cl.proyecto.morfologia.core.Morphology;
import cl.proyecto.morfologia.core.MorphologyParallel;
import cl.proyecto.morfologia.model.EdgePolicy;
import cl.proyecto.morfologia.model.Operation;
import cl.proyecto.morfologia.model.StructuringElement;
import cl.proyecto.morfologia.util.ImageIOUtils;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Clase que maneja la interacción con el usuario.
 * Permite ejecutar el programa en modo interactivo (consola) o con parámetros CLI.
 */
public class MenuRunner {

    public static void run(String[] args) {
        Map<String, String> map = parseArgs(args);
        if (map.isEmpty()) {
            interactive();
        } else {
            cli(map);
        }
    }

    /** Modo interactivo por consola */
    private static void interactive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Morfología Matemática ===");

        String mode = ask(sc, "Modo [seq/par]: ", "seq", Set.of("seq","par"));
        String op = ask(sc, "Operación [erosion/dilatacion]: ", "erosion", Set.of("erosion","dilatacion"));
        int se = Integer.parseInt(ask(sc, "Elemento estructurante [1..5]: ", "1", Set.of("1","2","3","4","5")));
        String edge = ask(sc, "Política de borde [ignore/pad]: ", "ignore", Set.of("ignore","pad"));
        int threads = Integer.parseInt(ask(sc, "Hilos (solo par) [e.g., 8]: ", "8", null));
        String in = ask(sc, "Imagen de entrada (PNG): ", null, null);
        String out = ask(sc, "Imagen de salida (PNG): ", "out.png", null);
        boolean bench = "s".equalsIgnoreCase(ask(sc, "Benchmark? [s/n]: ", "n", Set.of("s","n")));

        Map<String,String> map = new HashMap<>();
        map.put("mode", mode);
        map.put("op", op);
        map.put("se", Integer.toString(se));
        map.put("edge", edge);
        map.put("threads", Integer.toString(threads));
        map.put("in", in);
        map.put("out", out);
        if (bench) map.put("bench","true");
        cli(map);
    }

    /** Ejecución a partir de argumentos */
    private static void cli(Map<String, String> map) {
        try {
            String mode = map.getOrDefault("mode","seq");
            Operation op = Operation.from(map.getOrDefault("op","erosion"));
            int seId = Integer.parseInt(map.getOrDefault("se","1"));
            EdgePolicy edge = EdgePolicy.from(map.getOrDefault("edge","ignore"));
            int threads = Integer.parseInt(map.getOrDefault("threads","8"));
            boolean bench = Boolean.parseBoolean(map.getOrDefault("bench","false"));

            Path in = Path.of(Objects.requireNonNull(map.get("in"), "--in requerido"));
            Path out = Path.of(Objects.requireNonNull(map.get("out"), "--out requerido"));
            if (!Files.exists(in)) throw new IllegalArgumentException("No existe: " + in);

            BufferedImage img = ImageIOUtils.readPng(in.toFile()); 
            boolean[][] seMask = StructuringElement.build(seId);
            String seName = StructuringElement.name(seId);

            System.out.printf(Locale.ROOT,
                    "Modo=%s | Op=%s | SE=%s | Edge=%s | Threads=%d | In=%s | Out=%s%n",
                    mode, op, seName, edge, threads, in, out);

            BufferedImage result;

            if (bench) {
                Benchmark.Result r = "par".equalsIgnoreCase(mode)
                        ? Benchmark.runParallel(img, op, seMask, edge, threads)
                        : Benchmark.runSequential(img, op, seMask, edge);
                result = r.result();
                System.out.printf(Locale.ROOT,
                        "Tiempo promedio (ms): %.3f (σ=%.3f)  | runs=%d%n",
                        r.avgMs(), r.stdMs(), r.runs());
            } else {
                long t0 = System.nanoTime();
                if ("par".equalsIgnoreCase(mode)) {
                    result = MorphologyParallel.apply(img, op, seMask, edge, threads);
                } else {
                    result = Morphology.apply(img, op, seMask, edge);
                }
                long t1 = System.nanoTime();
                System.out.printf(Locale.ROOT, "Tiempo (ms): %.3f%n", (t1 - t0)/1e6);
            }

            ImageIOUtils.writePng(result, out.toFile());
            System.out.println("OK -> " + out.toAbsolutePath());

        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Parseo de argumentos tipo --clave valor */
    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i=0; i<args.length; i++) {
            if (args[i].startsWith("--") && i+1 < args.length && !args[i+1].startsWith("--")) {
                map.put(args[i].substring(2), args[i+1]);
                i++;
            } else if (args[i].startsWith("--") && (i+1==args.length || args[i+1].startsWith("--"))) {
                map.put(args[i].substring(2), "true");
            }
        }
        return map;
    }

    /** Utilidad para preguntar datos en consola */
    private static String ask(Scanner sc, String prompt, String def, Set<String> allowed) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (s.isEmpty() && def != null) s = def;
            if (allowed == null || allowed.contains(s.toLowerCase(Locale.ROOT))) return s;
            System.out.println("Valor no válido.");
        }
    }
}

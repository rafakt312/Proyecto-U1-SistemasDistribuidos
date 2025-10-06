package cl.proyecto.morfologia.core;

import cl.proyecto.morfologia.model.EdgePolicy;
import cl.proyecto.morfologia.model.Operation;
import cl.proyecto.morfologia.util.Timer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class Benchmark {

    public record Result(BufferedImage result, double avgMs, double stdMs, int runs) {}

    private static final int RUNS = 3; 

    public static Result runSequential(BufferedImage img, Operation op, boolean[][] se, EdgePolicy edge) {
        List<Double> times = new ArrayList<>(RUNS);
        BufferedImage out = null;
        for (int i=0;i<RUNS;i++){
            Timer t = new Timer();
            t.start();
            out = Morphology.apply(img, op, se, edge);
            double ms = t.ms();
            times.add(ms);
        }
        return new Result(out, avg(times), std(times), RUNS);
    }

    public static Result runParallel(BufferedImage img, Operation op, boolean[][] se, EdgePolicy edge, int threads) {
        List<Double> times = new ArrayList<>(RUNS);
        BufferedImage out = null;
        for (int i=0;i<RUNS;i++){
            Timer t = new Timer();
            t.start();
            try {
                out = MorphologyParallel.apply(img, op, se, edge, threads);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            double ms = t.ms();
            times.add(ms);
        }
        return new Result(out, avg(times), std(times), RUNS);
    }

    private static double avg(List<Double> xs){
        double s=0; for(double x:xs) s+=x; return s/xs.size();
    }
    private static double std(List<Double> xs){
        double m=avg(xs), s=0; for(double x:xs) s+=(x-m)*(x-m); return Math.sqrt(s/xs.size());
    }
}

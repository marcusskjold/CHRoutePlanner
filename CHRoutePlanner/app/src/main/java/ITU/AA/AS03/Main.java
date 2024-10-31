package ITU.AA.AS03;

import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    private static final long DEFAULT_SEED = 4263372046854775800L;
    private static final IllegalArgumentException ILLEGALALGORITHM = new IllegalArgumentException(
        "First argument must be one of: " + AlgorithmType.values());

    enum AlgorithmType {
        SIMPLEDIJKSTRA,
        EARLYSTOPDIJKSTRA,
        BIDIJKSTRA,
        CONTRACTIONHIERARCHIES
    }

    private static IntStream pairGen(long seed, int range) {
        Random r = new Random(seed);
        return r.ints(0, range);
    }

    public static void main(String[] args) {
        AlgorithmType a;

        try {
            a = AlgorithmType.valueOf(args[0]);
        } catch (IllegalArgumentException e) { throw ILLEGALALGORITHM; }

    }
}

package ChatServer.utilities;

import java.time.Instant;
import java.util.Random;

public class IDGenerator {
    private long seed;
    private Random random;

    public IDGenerator() {
        this.seed = Instant.EPOCH.getEpochSecond();
        this.random = new Random(this.seed);
    }

    public long nextID() {
        return this.random.nextLong();
    }

}

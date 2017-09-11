package tb.feed.impl;

import tb.feed.Feed;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomFeed implements Feed {

    private final Random random;
    private final long gap;
    private final TimeUnit timeUnit;

    public RandomFeed(long seed, long gap, TimeUnit timeUnit) {
        random = new Random(seed);
        this.gap = gap;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        pause();
        return String.join(",",
                String.valueOf(System.currentTimeMillis()),
                symbol(),
                String.format("%.2f", random.nextDouble() * 1000),
                String.valueOf(random.nextInt(399) + 1),
                flags());
    }

    private void pause() {
        try {
            timeUnit.sleep(gap);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String flags() {
        int size = random.nextInt(26) + 1;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        random.ints(0, 26).distinct().limit(size).sorted().forEach(value -> buffer.put((byte) ('A' + value)));
        byte[] dest = new byte[size];
        buffer.flip();
        buffer.get(dest);
        return new String(dest).intern();
    }

    private String symbol() {
        int characters = 3;
        byte[] bytes = new byte[6];
        bytes[0] = (byte) ('A' + random.nextInt(characters));
        bytes[1] = (byte) ('A' + random.nextInt(characters));
        bytes[2] = (byte) ('A' + random.nextInt(characters));
        bytes[3] = ' ';
        bytes[4] = (byte) ('A' + random.nextInt(characters));
        bytes[5] = (byte) ('A' + random.nextInt(characters));
        return new String(bytes).intern();
    }
}

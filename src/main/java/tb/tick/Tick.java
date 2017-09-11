package tb.tick;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public class Tick {

    private static final int TIMESTAMP_OFFSET = 0;
    private static final int SYMBOL_OFFSET = 8;
    private static final int PRICE_OFFSET = 14;
    private static final int SIZE_OFFSET = 18;
    private static final int FLAGS_OFFSET = 22;
    private static final int TICK_SIZE = 26;
    private static final int TIMESTAMP = 0;
    private static final int SYMBOL = 1;
    private static final int PRICE = 2;
    private static final int SIZE = 3;
    private static final int FLAGS = 4;
    private static final int LONG_SIZE = 8;
    private static final int SYMBOL_SIZE = 6;
    private static final int INTEGER_SIZE = 4;
    private static final int FLAGS_SIZE = 26;
    private static final int ASCII_A = 65;

    private final ByteBuffer buffer;

    private Tick(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public long getTimestamp() {
        return buffer.getLong(TIMESTAMP_OFFSET);
    }

    public String getSymbol() {
        byte[] bytes = new byte[SYMBOL_SIZE];
        buffer.position(SYMBOL_OFFSET);
        buffer.get(bytes);
        return new String(bytes).intern();
    }

    public double getPrice() {
        return buffer.getInt(PRICE_OFFSET) / 100.0;
    }

    public int getSize() {
        return buffer.getInt(SIZE_OFFSET);
    }

    public char[] getFlags() {
        CharBuffer flags = CharBuffer.allocate(FLAGS_SIZE);
        int count = 0;
        for (char flag = 'A'; flag <= 'Z'; flag++) {
            if (containsFlag(flag)) {
                flags.put(flag);
                count++;
            }
        }
        char[] dest = new char[count];
        flags.flip();
        flags.get(dest);
        return dest;
    }

    public boolean containsFlag(char flag) {
        int mask = 0x1 << (flag - ASCII_A);
        int flags = buffer.getInt(FLAGS_OFFSET);
        return (flags & mask) > 0;
    }

    @Override
    public String toString() {
        return "Tick{" +
                "timestamp=" + getTimestamp() +
                ", symbol='" + getSymbol() + '\'' +
                ", price=" + getPrice() +
                ", size=" + getSize() +
                ", flags=" + Arrays.toString(getFlags()) +
                '}';
    }

    public static Tick parse(String raw) {
        String[] fields = raw.split(",");
        return Tick.builder()
                .timestamp(extractTimestamp(fields))
                .symbol(extractSymbol(fields))
                .price(extractPrice(fields))
                .size(extractSize(fields))
                .flags(extractFlags(fields))
                .build();
    }

    private static char[] extractFlags(String[] fields) {
        return fields[FLAGS].toCharArray();
    }

    private static int extractSize(String[] fields) {
        return Integer.valueOf(fields[SIZE]);
    }

    private static int extractPrice(String[] fields) {
        return (int) Math.round(Double.parseDouble(fields[PRICE]) * 100);
    }

    private static String extractSymbol(String[] fields) {
        return fields[SYMBOL];
    }

    private static long extractTimestamp(String[] fields) {
        return Long.parseLong(fields[TIMESTAMP]);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ByteBuffer buffer = ByteBuffer.allocate(TICK_SIZE);
        private int bytesWritten = 0;

        public Builder timestamp(long timestamp) {
            buffer.putLong(TIMESTAMP_OFFSET, timestamp);
            bytesWritten += LONG_SIZE;
            return this;
        }

        public Builder symbol(String symbol) {
            buffer.position(SYMBOL_OFFSET);
            buffer.put(symbol.intern().getBytes());
            bytesWritten += SYMBOL_SIZE;
            return this;
        }

        public Builder price(int price) {
            buffer.putInt(PRICE_OFFSET, price);
            bytesWritten += INTEGER_SIZE;
            return this;
        }

        public Builder size(int size) {
            buffer.putInt(SIZE_OFFSET, size);
            bytesWritten += INTEGER_SIZE;
            return this;
        }

        public Builder flags(char[] flags) {
            int result = 0x0;
            for (char flag : flags) {
                int mask = 0x1 << (flag - ASCII_A);
                result |= mask;
            }
            buffer.putInt(FLAGS_OFFSET, result);
            bytesWritten += INTEGER_SIZE;
            return this;
        }

        public Tick build() {
            assert bytesWritten == TICK_SIZE : "Number of bytes does not match expected size of " + TICK_SIZE;
            return new Tick(buffer);
        }
    }
}
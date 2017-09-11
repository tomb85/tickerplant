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

    private final ByteBuffer buffer;

    private Tick(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public long getTimestamp() {
        return buffer.getLong(TIMESTAMP_OFFSET);
    }

    public String getSymbol() {
        byte[] bytes = new byte[6];
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
        CharBuffer flags = CharBuffer.allocate(26);
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
        int mask = 0x1 << (flag - 65);
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ByteBuffer buffer = ByteBuffer.allocate(TICK_SIZE);
        private int bytesWritten = 0;

        public Builder timestamp(long timestamp) {
            buffer.putLong(TIMESTAMP_OFFSET, timestamp);
            bytesWritten += 8;
            return this;
        }

        public Builder symbol(String symbol) {
            buffer.position(SYMBOL_OFFSET);
            buffer.put(symbol.intern().getBytes());
            bytesWritten += 6;
            return this;
        }

        public Builder price(int price) {
            buffer.putInt(PRICE_OFFSET, price);
            bytesWritten += 4;
            return this;
        }

        public Builder size(int size) {
            buffer.putInt(SIZE_OFFSET, size);
            bytesWritten += 4;
            return this;
        }

        public Builder flags(char[] flags) {
            int result = 0x0;
            for (char flag : flags) {
                int mask = 0x1 << (flag - 65);
                result |= mask;
            }
            buffer.putInt(FLAGS_OFFSET, result);
            bytesWritten += 4;
            return this;
        }

        public Tick build() {
            assert bytesWritten == TICK_SIZE : "Number of bytes does not match expected size of " + TICK_SIZE;
            return new Tick(buffer);
        }
    }
}
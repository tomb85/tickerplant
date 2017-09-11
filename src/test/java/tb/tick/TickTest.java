package tb.tick;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TickTest {

    private static Tick tick;

    private static long timestamp = 1505079642840L;
    private static String symbol = "XYZ LN";
    private static int price = 12234;
    private static int size = 200;
    private static char[] flags = new char[] {'B', 'Q'};

    @Before
    public void setup() {
        tick = Tick.builder()
                .timestamp(timestamp)
                .symbol(symbol)
                .price(price)
                .size(size)
                .flags(flags)
                .build();
    }

    @Test
    public void getTimestamp() throws Exception {
        assertEquals(timestamp, tick.getTimestamp());
    }

    @Test
    public void getSymbol() throws Exception {
        assertEquals(symbol, tick.getSymbol());
    }

    @Test
    public void getPrice() throws Exception {
        assertEquals(price / 100.0, tick.getPrice(), 0.01);
    }

    @Test
    public void getSize() throws Exception {
        assertEquals(size, tick.getSize());
    }

    @Test
    public void getFlags() throws Exception {
        assertArrayEquals(flags, tick.getFlags());
    }

    @Test
    public void containsFlag() throws Exception {
        assertTrue(tick.containsFlag('B'));
        assertTrue(tick.containsFlag('Q'));
        assertFalse(tick.containsFlag('Z'));
    }
}
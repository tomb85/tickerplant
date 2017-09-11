package tb.tick;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TickTest {

    private static Tick tick;

    private static final String RAW = "1505079642840,XYZ LN,122.2,200,BQ";
    private static final long EXPECTED_TIMESTAMP = 1505079642840L;
    private static final String EXPECTED_SYMBOL = "XYZ LN";
    private static final int EXPECTED_PRICE = 12220;
    private static final int EXPECTED_SIZE = 200;
    private static final char[] EXPECTED_FLAGS = new char[] {'B', 'Q'};

    @Before
    public void setup() {
        tick = Tick.parse(RAW);
    }

    @Test
    public void getTimestamp() throws Exception {
        assertEquals(EXPECTED_TIMESTAMP, tick.getTimestamp());
    }

    @Test
    public void getSymbol() throws Exception {
        assertEquals(EXPECTED_SYMBOL, tick.getSymbol());
    }

    @Test
    public void getPrice() throws Exception {
        assertEquals(EXPECTED_PRICE / 100.0, tick.getPrice(), 0.01);
    }

    @Test
    public void getSize() throws Exception {
        assertEquals(EXPECTED_SIZE, tick.getSize());
    }

    @Test
    public void getFlags() throws Exception {
        assertArrayEquals(EXPECTED_FLAGS, tick.getFlags());
    }

    @Test
    public void containsFlag() throws Exception {
        assertTrue(tick.containsFlag('B'));
        assertTrue(tick.containsFlag('Q'));
        assertFalse(tick.containsFlag('Z'));
    }
}
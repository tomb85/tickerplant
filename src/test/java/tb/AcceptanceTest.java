package tb;

import org.junit.Before;
import org.junit.Test;
import tb.feed.Feed;
import tb.feed.FeedHandler;
import tb.processor.Processor;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static tb.processor.functional.Accumulator.*;
import static tb.processor.functional.Filter.any;
import static tb.processor.functional.Filter.flag;

public class AcceptanceTest {

    public static final double EPSILON = 0.0000_0000_0001;
    private FeedHandler feedHandler;

    @Before
    public void setUp() throws URISyntaxException {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        feedHandler = new FeedHandler(feed);
    }

    @Test
    public void shouldCountTradesForInstrumentWithFlag() throws Exception {
        Processor<Integer> processor = new Processor<>("CBA AB", flag('Q'), tradeCount());
        feedHandler.subscribe(processor);
        feedHandler.start();
        assertThat(processor.getValue(), is(equalTo(16)));
    }

    @Test
    public void shouldCalculateAverageTradeSizeForInstrument() throws Exception {
        Processor<Double> processor = new Processor<>("AAA AC", any(), avgTradeSize());
        feedHandler.subscribe(processor);
        feedHandler.start();
        assertEquals(196.794871794871, processor.getValue(), EPSILON);
    }

    @Test
    public void shouldCalculateAverageTradePriceForInstrument() throws Exception {
        Processor<Double> processor = new Processor<>("AAA AC", any(), avgTradePrice());
        feedHandler.subscribe(processor);
        feedHandler.start();
        assertThat(processor.getValue() / 100.0, is(equalTo(431.615)));
    }

    @Test
    public void shouldCalculateLargestTradeSizeForInstrument() throws Exception {
        Processor<Integer> processor = new Processor<>("BBC AA", any(), maxTradeSize());
        feedHandler.subscribe(processor);
        feedHandler.start();
        assertThat(processor.getValue(), is(equalTo(379)));
    }

    @Test
    public void shouldCalculateSmallestTradeSizeForInstrument() throws Exception {
        Processor<Integer> processor = new Processor<>("BBC AA", any(), minTradeSize());
        feedHandler.subscribe(processor);
        feedHandler.start();
        assertThat(processor.getValue(), is(equalTo(2)));
    }

    private Path getResourcePath(String resource) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resource).toURI());
    }
}
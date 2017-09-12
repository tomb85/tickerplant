package tb;

import org.junit.Before;
import org.junit.Test;
import tb.feed.Feed;
import tb.feed.FeedHandler;
import tb.plant.TickerPlant;
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

    private TickerPlant tickerPlant;
    private FeedHandler feedHandler;

    @Before
    public void setUp() throws URISyntaxException {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        tickerPlant = new TickerPlant();
        feedHandler = new FeedHandler(feed, tickerPlant::onTick);
    }

    private void sendTrades() {
        feedHandler.start();
    }

    @Test
    public void shouldCountTradesForInstrumentWithFlag() throws Exception {
        Processor<Integer> processor = new Processor<>("CBA AB", flag('Q'), tradeCount());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertThat(processor.getValue(), is(equalTo(16)));
    }

    @Test
    public void shouldCountTradesForInstrument() throws Exception {
        Processor<Integer> processor = new Processor<>("AAA AC", any(), tradeCount());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertThat(processor.getValue(), is(equalTo(39)));
    }

    @Test
    public void shouldCalculateAverageTradeSizeForInstrument() throws Exception {
        Processor<Double> processor = new Processor<>("AAA AC", any(), avgTradeSize());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertEquals(196.794871794871, processor.getValue(), EPSILON);
    }

    @Test
    public void shouldCalculateAverageTradePriceForInstrument() throws Exception {
        Processor<Double> processor = new Processor<>("AAA AC", any(), avgTradePrice());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertEquals(561.00717948718, processor.getValue(), EPSILON);
    }

    @Test
    public void shouldCalculateLargestTradeSizeForInstrument() throws Exception {
        Processor<Integer> processor = new Processor<>("BBC AA", any(), maxTradeSize());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertThat(processor.getValue(), is(equalTo(379)));
    }

    @Test
    public void shouldCalculateSmallestTradeSizeForInstrument() throws Exception {
        Processor<Integer> processor = new Processor<>("BBC AA", any(), minTradeSize());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertThat(processor.getValue(), is(equalTo(2)));
    }

    @Test
    public void shouldCalculateTradeSizeTotalForInstrument() throws Exception {
        Processor<Long> processor = new Processor<>("AAA AC", any(), tradeSizeTotal());
        tickerPlant.subscribe(processor);
        sendTrades();
        assertThat(processor.getValue(), is(equalTo(7675L)));
    }

    @Test
    public void shouldHandleMultipleSubscribersForTheSameInstrument() throws Exception {
        Processor<Integer> first = new Processor<>("BBC AA", any(), minTradeSize());
        Processor<Integer> second = new Processor<>("BBC AA", any(), minTradeSize());
        tickerPlant.subscribe(first);
        tickerPlant.subscribe(second);
        sendTrades();
        assertThat(first.getValue(), is(equalTo(2)));
        assertThat(second.getValue(), is(equalTo(2)));
    }

    @Test
    public void shouldHandleMultipleSubscribersForDifferentInstrument() throws Exception {
        Processor<Integer> first = new Processor<>("BBC AA", any(), minTradeSize());
        Processor<Integer> second = new Processor<>("AAA AC", any(), tradeCount());
        tickerPlant.subscribe(first);
        tickerPlant.subscribe(second);
        sendTrades();
        assertThat(first.getValue(), is(equalTo(2)));
        assertThat(second.getValue(), is(equalTo(39)));
    }

    private Path getResourcePath(String resource) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resource).toURI());
    }
}
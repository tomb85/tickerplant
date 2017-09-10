package tb;

import org.junit.Assert;
import org.junit.Test;
import tb.feed.Feed;
import tb.feed.FeedHandler;
import tb.processor.Processor;
import tb.processor.functional.Accumulator;
import tb.processor.functional.Filter;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class AcceptanceTest {

    @Test
    public void testCountWithFlag() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);

        Processor<Integer> processor = new Processor<>("VOD LN", Runnable::run, Filter.flag('A'), Accumulator.count());

        feedHandler.subscribe(processor);

        feedHandler.start();

        Assert.assertThat(processor.getValue(), is(equalTo(2)));
    }

    @Test
    public void testAvgTradeSize() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);

        Processor<Double> processor = new Processor<>("VOD LN", Runnable::run, Filter.any(), Accumulator.avgSize());

        feedHandler.subscribe(processor);

        feedHandler.start();

        Assert.assertThat(processor.getValue(), is(equalTo(200.5)));
    }

    @Test
    public void testAvgPrice() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);

        Processor<Double> processor = new Processor<>("VOD LN", Runnable::run, Filter.any(), Accumulator.avgPrice());

        feedHandler.subscribe(processor);

        feedHandler.start();

        Assert.assertThat(processor.getValue() / 100.0, is(equalTo(431.615)));
    }

    @Test
    public void testMaxSize() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);
        Processor<Integer> processor = new Processor<>("VOD LN", Runnable::run, Filter.any(), Accumulator.maxSize());
        feedHandler.subscribe(processor);
        feedHandler.start();
        Assert.assertThat(processor.getValue(), is(equalTo(301)));
    }

    @Test
    public void testMinSize() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);
        Processor<Integer> processor = new Processor<>("VOD LN", Runnable::run, Filter.any(), Accumulator.minSize());
        feedHandler.subscribe(processor);
        feedHandler.start();
        Assert.assertThat(processor.getValue(), is(equalTo(100)));
    }

    private Path getResourcePath(String resource) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resource).toURI());
    }
}

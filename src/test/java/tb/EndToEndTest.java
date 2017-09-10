package tb;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class EndToEndTest {

    @Test
    public void testAvg() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);
        AverageTradeSize avgTrade = new AverageTradeSize("VOD LN"); // use factory | builder pass executor
        feedHandler.subscribe(avgTrade);

        feedHandler.start();

        Assert.assertThat(avgTrade.getValue(), is(equalTo(200.5)));
    }

    @Test
    public void testMaxSize() throws Exception {
        Feed feed = Feed.fromFile(getResourcePath("trades.csv"));
        FeedHandler feedHandler = new FeedHandler(feed, Runnable::run);
        MaxTradeSize avgTrade = new MaxTradeSize("VOD LN"); // use factory | builder pass executor
        feedHandler.subscribe(avgTrade);

        feedHandler.start();

        Assert.assertThat(avgTrade.getValue(), is(equalTo(301)));
    }



    private Path getResourcePath(String resource) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resource).toURI());
    }
}

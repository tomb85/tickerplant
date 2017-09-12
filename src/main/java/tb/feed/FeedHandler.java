package tb.feed;

import tb.tick.Tick;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FeedHandler {

    private final Feed feed;
    private final Executor executor;
    private final Consumer<Tick> tickerPlant;

    public static FeedHandler createAsync(Feed feed, Consumer<Tick> tickerPlant) {
        return new FeedHandler(feed, Executors.newSingleThreadExecutor(r -> new Thread(r, "feed-handler")), tickerPlant);
    }

    public static FeedHandler create(Feed feed, Consumer<Tick> tickerPlant) {
        return new FeedHandler(feed, Runnable::run, tickerPlant);
    }

    private FeedHandler(Feed feed, Executor executor, Consumer<Tick> tickerPlant) {
        this.feed = feed;
        this.executor = executor;
        this.tickerPlant = tickerPlant;
    }

    public void start() {
        executor.execute(() -> {
            while (feed.hasNext()) {
                String raw = feed.next();
                Tick tick = Tick.parse(raw);
                if (tick.isValid()) {
                    tickerPlant.accept(tick);
                }
            }
        });
    }
}
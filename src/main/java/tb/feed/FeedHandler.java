package tb.feed;

import tb.tick.Tick;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class FeedHandler {

    private final Feed feed;
    private final Executor executor;
    private final Consumer<Tick> tickerPlant;

    public FeedHandler(Feed feed, Executor executor, Consumer<Tick> tickerPlant) {
        this.feed = feed;
        this.executor = executor;
        this.tickerPlant = tickerPlant;
    }

    public FeedHandler(Feed feed, Consumer<Tick> tickerPlant) {
        this(feed, Runnable::run, tickerPlant);
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
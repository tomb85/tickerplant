package tb.feed;

import tb.tick.Tick;
import tb.tick.TickListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class FeedHandler {

    private final Feed feed;
    private final Executor executor;

    private final Map<String, List<TickListener>> listeners = new ConcurrentHashMap<>();

    public FeedHandler(Feed feed, Executor executor) {
        this.feed = feed;
        this.executor = executor;
    }

    public FeedHandler(Feed feed) {
        this(feed, Runnable::run);
    }

    public void subscribe(TickListener listener) {
        String symbol = listener.getSymbol();
        listeners.putIfAbsent(symbol, new CopyOnWriteArrayList<>());
        listeners.get(symbol).add(listener);
    }

    public void start() {
        executor.execute(() -> {
            while (feed.hasNext()) {
                String raw = feed.next();
                Tick tick = Tick.parse(raw);
                String symbol = tick.getSymbol();
                if (listeners.containsKey(symbol)) {
                    listeners.get(symbol).forEach(tickListener -> tickListener.onTick(tick));
                }
            }
        });
    }
}
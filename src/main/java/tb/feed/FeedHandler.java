package tb.feed;

import tb.tick.Tick;
import tb.tick.TickListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class FeedHandler {

    public static final int TIMESTAMP = 0;
    public static final int SYMBOL = 1;
    public static final int PRICE = 2;
    public static final int SIZE = 3;
    public static final int FLAGS = 4;

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
                Tick tick = process(raw);
                String symbol = tick.getSymbol();
                if (listeners.containsKey(symbol)) {
                    listeners.get(symbol).forEach(tickListener -> tickListener.onTick(tick));
                }
            }
        });
    }

    private Tick process(String raw) {
        String[] fields = raw.split(",");
        return Tick.builder()
                .timestamp(extractTimestamp(fields))
                .symbol(extractSymbol(fields))
                .price(extractPrice(fields))
                .size(extractSize(fields))
                .flags(extractFlags(fields))
                .build();
    }

    private char[] extractFlags(String[] fields) {
        return fields[FLAGS].toCharArray();
    }

    private int extractSize(String[] fields) {
        return Integer.valueOf(fields[SIZE]);
    }

    private int extractPrice(String[] fields) {
        return (int) Math.round(Double.parseDouble(fields[PRICE]) * 100);
    }

    private String extractSymbol(String[] fields) {
        return fields[SYMBOL];
    }

    private long extractTimestamp(String[] fields) {
        return Long.parseLong(fields[TIMESTAMP]);
    }
}
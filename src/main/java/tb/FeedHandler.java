package tb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class FeedHandler {

    public static final int TIMESTAMP = 0;
    public static final int SYMBOL = 1;
    public static final int PRICE = 2;
    public static final int SIZE = 3;
    public static final int FLAGS = 4;

    private final Feed feed;
    private final Executor executor;

    private final Map<String, TickListener> listeners = new ConcurrentHashMap<>();

    public FeedHandler(Feed feed, Executor executor) {
        this.feed = feed;
        this.executor = executor;
    }

    public void subscribe(TickListener tickListener) {
        listeners.putIfAbsent(tickListener.getSymbol(), tickListener);
    }

    public void start() {
        executor.execute(() -> {
            while (feed.hasNext()) {
                String raw = feed.next();
                Tick tick = process(raw);
                if (tick != null) {
                    TickListener listener = listeners.get(tick.getSymbol());
                    if (listener != null) {
                        listener.onTick(tick);
                    }
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
        return (int) (Double.parseDouble(fields[PRICE]) * 100);
    }

    private String extractSymbol(String[] fields) {
        return fields[SYMBOL];
    }

    private long extractTimestamp(String[] fields) {
        return Long.parseLong(fields[TIMESTAMP]);
    }
}
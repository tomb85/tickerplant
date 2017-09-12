package tb.plant;

import tb.tick.Tick;
import tb.tick.TickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TickerPlant {

    private final Executor executor;

    private Map<String, List<Tick>> ticks = new HashMap<>();
    private Map<String, List<TickListener>> listeners = new HashMap<>();

    public static TickerPlant createAsync() {
        return new TickerPlant(Executors.newSingleThreadExecutor(r -> new Thread(r, "ticker-plant")));
    }

    public static TickerPlant create() {
        return new TickerPlant(Runnable::run);
    }

    private TickerPlant(Executor executor) {
        this.executor = executor;
    }

    public void onTick(Tick tick) {
        executor.execute(() -> {
            String symbol = tick.getSymbol();
            ticks.putIfAbsent(symbol, new ArrayList<>());
            ticks.get(symbol).add(tick);
            fireUpdate(tick);
        });
    }

    private void fireUpdate(Tick tick) {
        String symbol = tick.getSymbol();
        if (listeners.containsKey(symbol)) {
            listeners.get(symbol).forEach(l -> l.onTick(tick));
        }
    }

    public void subscribe(TickListener listener) {
        subscribe(listener, false);
    }

    public void subscribeWithReplay(TickListener listener) {
        subscribe(listener, true);
    }

    private void subscribe(TickListener listener, boolean replay) {
        executor.execute(() -> {
            String symbol = listener.getSymbol();
            listeners.putIfAbsent(symbol, new ArrayList<>());
            listeners.get(symbol).add(listener);
            if (replay) {
                replay(listener);
            }
        });
    }

    private void replay(TickListener listener) {
        String symbol = listener.getSymbol();
        if (ticks.containsKey(symbol)) {
            ticks.get(symbol).forEach(t -> listener.onTick(t));
        }
    }
}
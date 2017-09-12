package tb.processor;

import tb.tick.Tick;
import tb.tick.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Processor<T extends Number> implements TickListener {

    private final String symbol;
    private final Executor executor;
    private final Predicate<Tick> filter;
    private final Function<Tick, T> accumulator;

    private final List<BiConsumer<String, T>> listeners = new ArrayList<>();

    private volatile T value;

    public static <T> Processor createAsync(String symbol, Predicate<Tick> filter, Function<Tick, T> accumulator) {
        return new Processor(symbol, Executors.newSingleThreadExecutor(), filter, accumulator);
    }

    public static <T> Processor create(String symbol, Predicate<Tick> filter, Function<Tick, T> accumulator) {
        return new Processor(symbol, Runnable::run, filter, accumulator);
    }

    private Processor(String symbol, Executor executor, Predicate<Tick> filter, Function<Tick, T> accumulator) {
        this.symbol = symbol;
        this.executor = executor;
        this.filter = filter;
        this.accumulator = accumulator;
    }

    public void subscribe(BiConsumer<String, T> listener) {
        executor.execute(() -> listeners.add(listener));
    }

    @Override
    public void onTick(Tick tick) {
        executor.execute(() -> {
            if (filter.test(tick)) {
                T current = value;
                value = accumulator.apply(tick);
                if (current != value) {
                    notifyListeners(value);
                }
            }
        });
    }

    private void notifyListeners(T value) {
        listeners.forEach(l -> l.accept(symbol, value));
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public T getValue() {
        return value;
    }
}

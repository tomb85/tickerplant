package tb.processor;

import tb.tick.Tick;
import tb.tick.TickListener;

import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;

public class Processor<T> implements TickListener {

    private final String symbol;
    private final Executor executor;
    private final Predicate<Tick> filter;
    private final Function<Tick, T> accumulator;

    private volatile T value;

    public Processor(String symbol, Executor executor, Predicate<Tick> filter, Function<Tick, T> accumulator) {
        this.symbol = symbol;
        this.executor = executor;
        this.filter = filter;
        this.accumulator = accumulator;
    }

    public Processor(String symbol, Predicate<Tick> filter, Function<Tick, T> accumulator) {
        this(symbol, Runnable::run, filter, accumulator);
    }

    @Override
    public void onTick(Tick tick) {
        executor.execute(() -> {
            if (filter.test(tick)) {
                value = accumulator.apply(tick);
            }
        });
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public T getValue() {
        return value;
    }
}

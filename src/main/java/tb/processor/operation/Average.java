package tb.processor.operation;

import tb.tick.Tick;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Average<T> extends Operation<T, Double> {

    private final BiFunction<T, T, T> adder;
    private final BiFunction<T, Integer, Double> divider;

    private int count = 0;
    private T total;

    public Average(
            Function<Tick, T> extractor,
            BiFunction<T, T, T> adder,
            BiFunction<T, Integer, Double> divider,
            Supplier<T> initialValueSupplier) {
        super(extractor);
        this.adder = adder;
        this.divider = divider;
        total = initialValueSupplier.get();
    }

    @Override
    protected Double accumulate(T next) {
        total = adder.apply(total, next);
        return divider.apply(total, ++count);
    }
}

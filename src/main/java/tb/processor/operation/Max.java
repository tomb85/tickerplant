package tb.processor.operation;

import tb.tick.Tick;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;

public class Max<T> extends Operation<T, T> {

    private final Comparator<T> comparator;

    private T max;

    public Max(Function<Tick, T> extractor, Comparator<T> comparator, Supplier<T> initialValueSupplier) {
        super(extractor);
        this.comparator = comparator;
        max = initialValueSupplier.get();
    }

    @Override
    protected T accumulate(T next) {
        if (comparator.compare(max, next) < 0) {
            max = next;
        }
        return max;
    }
}

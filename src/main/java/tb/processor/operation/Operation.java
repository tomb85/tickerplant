package tb.processor.operation;

import tb.tick.Tick;

import java.util.function.Function;

public abstract class Operation<T, R> {

    private final Function<Tick, T> extractor;

    public Operation(Function<Tick, T> extractor) {
        this.extractor = extractor;
    }

    public R accumulate(Tick tick) {
        T next = extractor.apply(tick);
        return accumulate(next);
    }

    protected abstract R accumulate(T next);

}

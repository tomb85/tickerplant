package tb.processor.operation;

import tb.tick.Tick;

import java.util.function.Function;

public class Count<T> extends Operation<T, Integer>{

    private int count;

    public Count(Function<Tick, T> extractor) {
        super(extractor);
    }

    @Override
    protected Integer accumulate(T next) {
        return ++count;
    }
}

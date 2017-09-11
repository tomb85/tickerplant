package tb.processor.operation;

import tb.tick.Tick;

import java.util.function.Function;

public class Sum extends Operation<Integer, Long> {

    private long total = 0;

    public Sum(Function<Tick, Integer> extractor) {
        super(extractor);
    }

    @Override
    protected Long accumulate(Integer next) {
        total += next;
        return total;
    }
}

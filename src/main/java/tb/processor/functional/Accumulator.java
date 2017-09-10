package tb.processor.functional;

import tb.processor.operation.Average;
import tb.processor.operation.Count;
import tb.processor.operation.Max;
import tb.tick.Tick;

import java.util.Comparator;
import java.util.function.Function;

public class Accumulator {

    public static Function<Tick, Integer> maxSize() {
        return new Max<>(
                Tick::getSize,
                Integer::compare,
                () -> 0
        )::accumulate;
    }

    public static Function<Tick, Integer> minSize() {
        return new Max<>(
                Tick::getSize,
                reversed(Integer::compare),
                () -> Integer.MAX_VALUE
        )::accumulate;
    }

    public static Function<Tick, Double> avgSize() {
        return new Average<>(
                Tick::getSize,
                (first, second) -> first + second,
                (total, count) -> total / (double) count,
                () -> 0
        )::accumulate;
    }

    public static Function<Tick, Double> avgPrice() {
        return new Average<>(
                Tick::getPrice,
                (first, second) -> first + second,
                (total, count) -> total / (double) count,
                () -> 0
        )::accumulate;
    }

    public static Function<Tick, Integer> count() {
        return new Count<>(
                tick -> null
        )::accumulate;
    }

    private static <T> Comparator<T> reversed(Comparator<T> comparator) {
        return (o1, o2) -> comparator.compare(o1, o2) * -1;
    }
}

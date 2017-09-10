package tb.processor.functional;

import tb.tick.Tick;

import java.util.function.Predicate;

public class Filter {

    public static Predicate<Tick> any() {
        return tick -> true;
    }

    public static Predicate<Tick> flag(char flag) {
        return tick -> tick.containsFlag(flag);
    }
}

package tb;

public class MaxTradeSize implements TickListener {

    private final String symbol;

    private volatile int max = 0;

    public MaxTradeSize(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void onTick(Tick tick) {
        int size = tick.getSize();
        if (size > max) {
            max = size;
        }
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public int getValue() {
        return max;
    }
}

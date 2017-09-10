package tb;

public class AverageTradeSize implements TickListener {

    private final String symbol;

    private double sum = 0;
    private int count = 0;

    private volatile double value = 0;

    public AverageTradeSize(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void onTick(Tick tick) {
        count++;
        sum += tick.getSize();
        value = sum / count;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public double getValue() {
        return value;
    }
}

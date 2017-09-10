package tb.tick;

public interface TickListener {

    void onTick(Tick tick);

    String getSymbol();
}

package tb;

public interface TickListener {

    void onTick(Tick tick);

    String getSymbol();
}

package tb.plant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tb.tick.Tick;
import tb.tick.TickListener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TickerPlantTest {

    private TickerPlant tickerPlant;

    @Before
    public void setup() {
        tickerPlant = new TickerPlant();
        tickerPlant.onTick(Tick.parse("1505079642830,AAC CB,998.92,327,ABCDEFGHIJKLNOQRSTUVWXYZ"));
        tickerPlant.onTick(Tick.parse("1505079642831,AAC AC,904.50,76,GORY"));
        tickerPlant.onTick(Tick.parse("1505079642833,AAC BC,424.52,107,ABCDEFHJKLMNQRSTUVWXYZ"));
        tickerPlant.onTick(Tick.parse("1505079642834,AAC BC,794.87,29,BEKLY"));
    }

    @Test
    public void subscribe() {
        TickListener listener = Mockito.mock(TickListener.class);
        when(listener.getSymbol()).thenReturn("AAC BC");
        tickerPlant.subscribe(listener);
        verify(listener, never()).onTick(any());
    }

    @Test
    public void subscribeWithReplay() {
        TickListener listener = Mockito.mock(TickListener.class);
        when(listener.getSymbol()).thenReturn("AAC BC");
        tickerPlant.subscribeWithReplay(listener);
        verify(listener, times(2)).onTick(any());
    }
}
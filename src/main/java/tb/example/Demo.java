package tb.example;

import tb.feed.Feed;
import tb.feed.FeedHandler;
import tb.plant.TickerPlant;
import tb.processor.Processor;
import tb.processor.functional.Accumulator;
import tb.processor.functional.Filter;

import java.util.function.BiConsumer;

public class Demo {

    public static void main(String[] args) {

        // Create random feed and feed handler
        Feed feed = Feed.defaultRandom();
        TickerPlant tickerPlant = TickerPlant.createAsync();
        FeedHandler feedHandler = FeedHandler.createAsync(feed, tickerPlant::onTick);
        feedHandler.start();

        // Create average price calculator and subscribe with replay for updates
        Processor<Double> averagePrice = Processor.createAsync("AAA BC", Filter.any(), Accumulator.avgTradePrice());
        averagePrice.subscribe(bind(Demo::handle, "avg-price"));
        tickerPlant.subscribeWithReplay(averagePrice);

        // Create maximum trade size calculator and subscribe for updates
        Processor<Integer> maxTradeSize = Processor.createAsync("AAA BC", Filter.any(), Accumulator.maxTradeSize());
        maxTradeSize.subscribe(bind(Demo::handle, "max-size"));
        tickerPlant.subscribe(maxTradeSize);
    }

    private static <T> void handle(String symbol, T value, String name) {
        System.out.println(name + " [" + symbol + "] " + String.format("%.2f", value));
    }

    private static void handle(String symbol, Integer value, String name) {
        System.out.println(name + " [" + symbol + "] " + value);
    }

    private static <T> BiConsumer<String, T> bind(ProcessorHandler<T> handler, String name) {
        return (s, t) -> handler.handle(s, t, name);
    }

    @FunctionalInterface
    interface ProcessorHandler<T> {
        void handle(String symbol, T value, String name);
    }
}
package tb.example;

import tb.feed.Feed;
import tb.feed.FeedHandler;
import tb.processor.Processor;
import tb.processor.functional.Accumulator;
import tb.processor.functional.Filter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {

    public static void main(String[] args) {

        // Create random feed and feed handler
        Feed feed = Feed.defaultRandom();
        FeedHandler feedHandler = new FeedHandler(feed, executor("feed-handler-thread"));
        feedHandler.start();

        // Create average price calculator and subscribe for updates
        Processor<Double> averagePrice = new Processor<>("AAA BC", executor("avg-price-thread"), Filter.any(), Accumulator.avgTradePrice());
        averagePrice.subscribe(Demo::handle);
        feedHandler.subscribe(averagePrice);

        // Create maximum trade size calculator and subscribe for updates
        Processor<Integer> maxTradeSize = new Processor<>("AAA BC", executor("max-size-thread"), Filter.any(), Accumulator.maxTradeSize());
        maxTradeSize.subscribe(Demo::handle);
        feedHandler.subscribe(maxTradeSize);
    }

    private static <T> void handle(String symbol, T value) {
        System.out.println(Thread.currentThread().getName() + " [" + symbol + "] " + String.format("%.2f", value));
    }

    private static void handle(String symbol, Integer value) {
        System.out.println(Thread.currentThread().getName() + " [" + symbol + "] " + value);
    }

    private static ExecutorService executor(String name) {
        return Executors.newSingleThreadExecutor(r -> new Thread(r, name));
    }
}
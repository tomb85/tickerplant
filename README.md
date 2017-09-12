Tickerplant
===========

Assumptions
-----------

The following assumptions have been made about the inbound trade feed

- represented as comma-delimited string
- symbol is a combination of 3 alpha-numeric characters followed by a space and then followed by 2 letters, e.g. 'A12 LN'
- there is at least one flag present
- all flags are unique
- flags can only be upper case letters
- flags are represented as string without any delimitation, e.g. 'XYZ'

Components
----------

#### Feed

Stream of strings each representing a trade

#### Feed Handler

Converts raw `Feed` into a stream of `Tick` objects and passes to `TickerPlant`

#### Ticker Plant

Aggregates incoming `Tick` events by symbol. Allows to `subscribe` and `subscribeWithReplay` for a given symbol

#### Processor

Represents a consumer of `TickerPlant` events. Is composed of `Filter` and `Accumulator` functions. The following `Operation`s are currently available

- `Min` and `Max`
- `Average`
- `Count`
- `Sum`

Testing
-------

Tests can be run by typing in the project root directory

    mvn clean test

This will execute `AcceptanceTest` as well as unit tests

Building and running examples
-----------------------------

To run a simple `Demo` that will create a `RandomFeed` and subscribe for `Tick` events type in the project root directory

    mvn clean package
    cd target
    java -jar ticker-plant-1.0-SNAPSHOT.jar

This will produce output similar to this

    avg-price [AAA BC] 645.68
    max-size [AAA BC] 8
    max-size [AAA BC] 73
    avg-price [AAA BC] 630.00
    avg-price [AAA BC] 695.99
    max-size [AAA BC] 344
    avg-price [AAA BC] 644.46
    avg-price [AAA BC] 652.56

Extending
---------

In order to add a new type of `Operation` we can subclass the base class and provide custom logic. This can be demonstrated on the `Sum` example

```java
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
```

It is always a good idea to add a static factory method in the `Accumulator` class that will return a function calculating total size of trades

```java
public static Function<Tick, Long> tradeSizeTotal() {
    return new Sum(Tick::getSize)::accumulate;
}
```

We can then use `Sum` in the following way

```java
Processor<Integer> tradeTotal = Processor.createAsync("AAA BC", Filter.any(), Accumulator.tradeSizeTotal());
tradeTotal.subscribe((symbol, value) -> System.out.print("Total size of trades for symbol " + symbol + ": " + value));
tickerPlant.subscribe(tradeTotal);
```
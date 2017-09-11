package tb.feed;


import tb.feed.impl.CsvFeed;
import tb.feed.impl.RandomFeed;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public interface Feed {

    static Feed fromFile(Path path) {
        return new CsvFeed(path);
    }

    static Feed defaultRandom() {
        return new RandomFeed(42, 1, TimeUnit.MILLISECONDS);
    }

    boolean hasNext();

    String next();
}

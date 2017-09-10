package tb.feed;


import tb.feed.impl.CsvFeed;

import java.nio.file.Path;

public interface Feed {

    static Feed fromFile(Path path) {
        return new CsvFeed(path);
    }

    boolean hasNext();

    String next();
}
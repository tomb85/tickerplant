package tb.feed.impl;

import tb.feed.Feed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvFeed implements Feed {

    private final Path path;
    private List<String> data;

    private int pos = 0;

    public CsvFeed(Path path) {
        this.path = path;
        data = getData(path);
    }

    private List<String> getData(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return pos < data.size();
    }

    @Override
    public String next() {
        return data.get(pos++);
    }
}

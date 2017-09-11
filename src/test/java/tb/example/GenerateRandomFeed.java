package tb.example;

import tb.feed.Feed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class GenerateRandomFeed {

    public static void main(String[] args) throws IOException {
        Feed feed = Feed.defaultRandom();
        int size = 10_000;
        List<String> lines = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String raw = feed.next();
            lines.add(raw);
        }
        show(lines);
        persist(lines);
    }

    private static void persist(List<String> lines) throws IOException {
        Path path = Paths.get("random_trades.csv");
        Files.write(path, lines, WRITE, CREATE);
    }

    private static void show(List<String> lines) {
        Map<String, Long> collect = lines.stream().map(s -> s.split(",")[1]).collect(Collectors.groupingBy(o -> o.toString(), Collectors.counting()));
        System.out.println(collect);
    }
}

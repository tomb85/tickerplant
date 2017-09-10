package tb.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class GenerateRandomFeed {

    private static Random random = new Random(42);

    public static void main(String[] args) throws InterruptedException, IOException {
        int size = 10_000;
        List<String> lines = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String line = String.join(",",
                    String.valueOf(System.currentTimeMillis()),
                    symbol(),
                    String.format("%.2f", random.nextDouble() * 1000),
                    String.valueOf(random.nextInt(400)),
                    flags());

            Thread.sleep(1);
            lines.add(line);
        }

        Map<String, Long> collect = lines.stream().map(s -> s.split(",")[1]).collect(Collectors.groupingBy(o -> o.toString(), Collectors.counting()));
        System.out.println(collect);

        Path path = Paths.get("random_trades.csv");
        Files.write(path, lines, WRITE, CREATE);
    }

    private static String flags() {
        int size = random.nextInt(26) + 1;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        random.ints(0, 26).distinct().limit(size).sorted().forEach(value -> buffer.put((byte) ('A' + value)));
        byte[] dest = new byte[size];
        buffer.flip();
        buffer.get(dest);
        return new String(dest).intern();
    }

    private static String symbol() {
        int characters = 3;
        byte[] bytes = new byte[6];
        bytes[0] = (byte) ('A' + random.nextInt(characters));
        bytes[1] = (byte) ('A' + random.nextInt(characters));
        bytes[2] = (byte) ('A' + random.nextInt(characters));
        bytes[3] = ' ';
        bytes[4] = (byte) ('A' + random.nextInt(characters));
        bytes[5] = (byte) ('A' + random.nextInt(characters));
        return new String(bytes).intern();
    }
}

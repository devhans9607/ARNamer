import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

//@Slf4j
public class ARNamer {
    static String HYPHEN = "-";
    static String DOT = ".";
    static String SLASH = "/";
    static String FORMAT = "m4a";
    static String DOT_HYPHEN_REGEX = "[-.]";
    static String DATE_REGEX = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])(0[0-9]|1[0-9]|2[0-4])([0-5][0-9])([0-5][0-9])";


    public static void main(String[] args) throws IOException {
        String path = System.getProperty("path");
        changeFiles(path);
    }

    static void changeFiles(String path) throws IOException {
        int[] inf = {0, 0, 0};

        File dir = new File(path);

        if(dir.exists()) {
            File[] files = dir.listFiles();

            if (files == null) {
                System.out.println("!!!Null File list");
                return;
            }

            System.out.println("[START] " + files.length + " items");

            for (File file : files) {
                if (!Files.isDirectory(file.toPath())) {
                    String resultMsg;
                    String oldName = file.getName();
                    String newName = changeName(file.getName(), path);

                    resultMsg = (newName == null)
                            ? "Error(1) - " + oldName
                            : file.renameTo(new File(newName))
                                ? "Success - " + oldName + " -------> " + newName
                                : "Error(2) - " + oldName;

                    inf[resultMsg.contains("Success") ? 0 : 1]++;
                    System.out.println(resultMsg);
                } else {
                    inf[2]++;
                }
            }
            System.out.println("[END] " + inf[0] + " successes / " + inf[1] + " skips / " + inf[2] + " folders");
        } else {
            System.out.println("!!!Not a directory");
        }
    }

    static String changeName(String oldName, String path) throws IOException {
        String[] splits = oldName.split(DOT_HYPHEN_REGEX);
        int finalIndex = splits.length - 1;

        if (finalIndex < 9 || !splits[finalIndex].equals(FORMAT) || Pattern.matches(DATE_REGEX, Arrays.asList(splits).subList(finalIndex - 6, finalIndex).toString()))
            return null;

        Collections.rotate(Arrays.asList(splits).subList(0, finalIndex), 6);

        StringBuilder result = new StringBuilder();
        AtomicInteger count = new AtomicInteger();

        result.append(path).append(SLASH)
                .append(splits[0]).append(SLASH)
                .append(splits[1]);

        Path newPath = Paths.get(result.toString());
        if (!Files.isDirectory(newPath))
            Files.createDirectories(newPath);

        result.append(SLASH);

        Stream.of(splits).forEach(next -> {
            result.append(next);
            int index = count.getAndIncrement();
            if (index == (finalIndex - 1)) {
                result.append(DOT);
            } else if (index == 2 || (index >= 5 && index < finalIndex -1)) {
                result.append(HYPHEN);
            }
        });
        return result.toString();
    }
}

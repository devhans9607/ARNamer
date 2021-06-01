import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Slf4j
public class ARNamer {
    static int NOT_NUM = -9999;
    static int DATE_DIGITS = 6;
    static int DATE_SEPARATE = 2;
    static String HYPHEN = "-";
    static String DOT = ".";

    public static void main(String[] args) throws IOException {
        //        renaming();
        String path = System.getProperty("path");
        changeFiles(path);
    }

    static void changeFiles(String path) throws IOException {
        int count = 0;
        int folders = 0;
        int error = 0;

        File dir = new File(path);

        if(dir.exists()) {
            File[] files = dir.listFiles();

            if (files == null) {
                System.out.println("Null File list");
                return;
            }

            System.out.println("[START] " + files.length + " files");

            for (File file : files) {
                if (!Files.isDirectory(file.toPath())) {
                    String resultMsg;
                    String oldName = file.getName();
                    String newName = changeName(file.getName());

                    resultMsg = (newName == null)
                            ? "Error(1) - " + oldName
                            : file.renameTo(new File(dirConfig(newName, path)))
                            ? "Success - " + oldName + " -------> " + newName
                            : "Error(2) - " + oldName;

                    if (resultMsg.contains("Success")) {
                        count++;
                    } else {
                        error++;
                    }

                    System.out.println(resultMsg);
                } else {
                    folders++;
                }
            }
            System.out.println("[END] " + count + " successes / " + error + " errors / " + folders + " folders");
        } else {
            System.out.println("Not a directory");
        }
    }

    static String changeName(String oldName) {
        String[] nameAndFormat = oldName.split(Pattern.quote(DOT));

        if (nameAndFormat.length != 2)
            return null;

        String[] parts = nameAndFormat[0].split(Pattern.quote(HYPHEN));
        int num = parts.length;

        if (num < 8 || isNumeric(parts[0]) != NOT_NUM)
            return null;

        int dateStart = num - DATE_DIGITS;

        StringBuilder result = new StringBuilder();

        for (int i = dateStart; i < num; i++){
            result.append(parts[i]);
            if (i == dateStart + DATE_SEPARATE || i == num -1)
                result.append(HYPHEN);
        }

        for (int j = 0; j < dateStart; j++){
            result.append(parts[j]);
            if (j != dateStart -1)
                result.append(HYPHEN);
        }

        result.append(DOT).append(nameAndFormat[1]);
        return result.toString();
    }

    static String dirConfig(String name, String path) throws IOException {
        int year = isNumeric(name.substring(0, 4));
        int month = isNumeric(name.substring(4,6));

        if (year > 2200 || year < 2000 || month < 0 || month > 12)
            return path + "/" + name;

        String dir = path + "/" + year + "/" + month;

        Path dstPath = Paths.get(dir);
        if (!Files.isDirectory(dstPath))
            Files.createDirectories(dstPath);

        return dstPath + "/" + name;
    }

    static int isNumeric(String string) {
        if(string == null || string.equals(""))
            return NOT_NUM;

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return NOT_NUM;
        }
    }


//    static void renaming() {
//        File dir = new File(pathInput);
//        if(dir.exists()) {
//            File[] files = dir.listFiles();
//            for (File file : files) {
//                if (!Files.isDirectory(file.toPath())){
//                    String fileName = file.getName();
//                    if(fileName.contains("FaceTime") || fileName.contains("TelephonyCall")){
//                        file.renameTo(new File(pathInput + "/" + fileName.replace("FaceTime", "FT").replace("TelephonyCall", "Tel")));
//                    }
//                }
//            }
//        }
//    }
}

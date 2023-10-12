import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class ParseJson
{
    public static final String pathJsonAndCsv = "data/data";

    public static JSONArray getJsonFiles() {

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();

        try {
            Files.walk(Paths.get(pathJsonAndCsv), FileVisitOption.FOLLOW_LINKS)
                    .forEach(file -> {
                        if (file.toFile().isFile() && file.toFile().getPath().endsWith(".json")) {
                            try {
                                StringBuilder builder = new StringBuilder();
                                List<String> lines = Files.readAllLines(file);
                                for (String line : lines) {
                                    builder.append(line);
                                }
                                jsonArray.add(parser.parse(builder.toString()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonArray;
    }
}


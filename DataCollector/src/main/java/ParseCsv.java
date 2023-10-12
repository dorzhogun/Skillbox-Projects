import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ParseCsv {

    public static final String pathJsonAndCsv = "data/data";

    public static JSONArray getCsvFiles ()
    {
        JSONArray arrayOfDataFromCsv = new JSONArray();
        try {
            Files.walk(Paths.get(pathJsonAndCsv), FileVisitOption.FOLLOW_LINKS)
                    .forEach(file -> {
                        if (file.toFile().isFile() && file.toFile().getPath().endsWith(".csv")) {
                            List<String> lines;
                            try {
                                lines = Files.readAllLines(file.toAbsolutePath());
                                for (String line : lines) {
                                    String[] tokens = line.split(",");
                                    JSONObject dataFromCsv = new JSONObject();
                                    for(int i = 0; i < tokens.length - 1; i++) {
                                        if (tokens[i].equalsIgnoreCase("name") || tokens[i].equalsIgnoreCase("date")) {
                                            continue;
                                        }
                                        dataFromCsv.put("name", tokens[i]);
                                        dataFromCsv.put("date", tokens[i + 1]);
                                        arrayOfDataFromCsv.add(dataFromCsv);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return arrayOfDataFromCsv;
    }
}

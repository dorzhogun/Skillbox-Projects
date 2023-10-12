import core.Line;
import core.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class WebParser {

    private static final StationIndex stationIndex = new StationIndex();

    // ����� ��� �������� ���-�������� � ������ ���� ����� � ���� html
        public static void getHtmlCodeFile() {
            String htmlPage = "https://skillbox-java.github.io/";
            String pathHtmlCode = "data/code.html";
            try {
                String html = Jsoup.connect(htmlPage).get().html();
                PrintWriter writer = new PrintWriter(pathHtmlCode, StandardCharsets.UTF_8);
                writer.write(html);
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    // ����� �������� stationIndex � ������� � ������ � �������� �����
    public static StationIndex getStationIndex()
    {
        getHtmlCodeFile();
        try
        {
            parseLines().forEach((key, value) -> stationIndex.addLine(new Line(key, value)));

            for (Map.Entry<String, List<String>> item : parseStations().entrySet()) {
                String lineNumber = item.getKey();
                for (int i = 0; i < item.getValue().size(); i++)
                {
                    Station station = new Station(item.getValue().get(i), stationIndex.getLine(lineNumber));
                    parseConnections().forEach(connection -> {
                        if (station.getName().equalsIgnoreCase(connection)) {
                            station.setHasConnection(true);
                        }
                    });
                    for (Object objectFromCsv : ParseCsv.getCsvFiles()) {
                        JSONObject dateObject = (JSONObject) objectFromCsv;
                        String name = dateObject.get("name").toString();
                        String date = dateObject.get("date").toString();
                        if (name.equalsIgnoreCase(station.getName())) {
                            station.setDate(date);
                        }
                    }

                    for (Object objectFromJson : ParseJson.getJsonFiles()) {
                        JSONArray arrayFromJson = (JSONArray) objectFromJson;
                        for (Object value : arrayFromJson) {
                            JSONObject jsonObject = (JSONObject) value;
                            String name = jsonObject.get("station_name").toString();
                            String depth = String.valueOf(jsonObject.get("depth"));
                            depth = depth.substring(depth.indexOf(',') + 1)
                                    .replaceAll(",", ".").replaceAll("-", "");

                            if (name.equalsIgnoreCase(station.getName()) && !depth.equalsIgnoreCase("?"))
                            {
                                double dp = Double.parseDouble(depth);
                                station.setDepth(dp);
                            }
                        }
                    }
                    stationIndex.getLine(lineNumber).addStation(station);
                    stationIndex.addStation(station);
                }
            }
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        return stationIndex;
    }

    public static Map<String, String> parseLines()
    {
        // ������� linesMap: ���� - ����� �����, �������� - ��� �����
        Map<String, String> linesMap = new HashMap<>();
        // �������� �������� ����� ����� � ��������� ������ � linesMap
        Elements linesHtml = getDocumentElements().select(".js-metro-line");
        linesHtml.forEach(l -> linesMap.put(l.attr("data-line"), l.text()));
        return linesMap;
    }

    public static Map<String, List<String>> parseStations()
    {
        // ������� stationsMap: ���� - ����� �����, �������� - ������ �������
        Map<String, List<String>> stationsMap = new HashMap<>();
        // �������� ��������-������� ����� � ��������� ������ � stationsMap �� ������ �����
        Elements stationsHtml = getDocumentElements().select(".js-metro-stations");
        stationsHtml.forEach(s ->
        {
            List<String> stationsList = new ArrayList<>();
            String lineNumber = s.attr("data-line");
            for (int i = 0; i < s.children().size(); i++)
            {
                stationsList.add(s.children().get(i).text()
                        .replaceAll("[0-9+.]", "")
                        .trim());
            }
            stationsMap.put(lineNumber, stationsList);
        });
        return stationsMap;
    }

    public static TreeSet<String> parseConnections()
    {
        // ������� TreeSet ��� ������ �������, � ������� ���� ���� �� 1 ������� �� ������� ������ �����
        TreeSet<String> connectedStations = new TreeSet<>();

        try {
            // ������� ����� stationsMap � ������� � �������� �����
            Map<String, List<String>> stationsMap = parseStations();
            // ��������� ����� ������ ���� ������� �����
            List<String> allStationsList = stationsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            // �������� �������� � ������� � ���������
            Elements connectionsHtml = getDocumentElements().select(".t-icon-metroln");
            connectionsHtml.forEach(c -> {
                // ������� ��� ������ � ������� � ��������� - ���������� �� ������� ���� �������
                // ���� ������ � ������� � ��������� �������� ��� ������� �� ������ ������ �������,
                // �� ��������� ��� ������� � TreeSet � ���������� ������� ������� ���� �� � 1 ���������
                String stringAboutConnection = c.attr("title");
                for (String station : allStationsList) {
                    if (stringAboutConnection.contains(station)) {
                        connectedStations.add(station);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connectedStations;
    }

    // ��������� ����� ��� �������� ������ �� ����� �� �������
    public static String parseFile(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> builder.append(line).append("\n"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    // ��������� ����� ��������� ��������� � ����������
    public static Document getDocumentElements()
    {
        String htmlFile = parseFile("data/code.html"); // ������ ���� html � ��������� � ������
        return Jsoup.parse(htmlFile); // ������ ������ � �������� �������� � ����������
    }
}




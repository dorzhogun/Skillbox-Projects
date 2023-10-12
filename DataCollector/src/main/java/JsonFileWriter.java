import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.PrintWriter;
import java.util.*;

public class JsonFileWriter {

    private static final String pathMapJson = "data/map.json";
//    private static int totalCount;
    private static final String pathStationsJson = "data/stations.json";

    // �������� � ����� ������ �� WebParser
    // ����� �������� � ������ map.json
    public static void createMapJson() {
        try {

            // ������� ������� ������ ����� ����� ��� map.json
            JSONObject jo = new JSONObject();
            // ������� ������-������ ���� - ����� �����, �������� - ��� �����
            JSONArray lineNumberAndName = new JSONArray();
            // ������� map � ������� � ������ �� ����� WebParser
            // � ������-������ � ������� � ����� ��������� ������� � ������ - number/name, ��������� - �����/��� �����
            WebParser.parseLines().forEach((key, value) -> {
                JSONObject linesInfo = new JSONObject();
                String nu = "number";
                String na = "name";
                linesInfo.put(nu, key);
                linesInfo.put(na, value);
                lineNumberAndName.add(linesInfo);
            });

            // � ������� ������ ������ � �������� �������� ������-������ � ������� � ������,
            // ���� - ������ "lines"
            jo.put("lines", lineNumberAndName);

            // ������� map � ������� � �������� �� WebParser ������ parseStations
            // ��������� � ������� stationsObject ���� - ����� �����, �������� - ������ ������� ���� �����
            JSONObject stationsObject = new JSONObject();
            for (Map.Entry<String, List<String>> item : WebParser.parseStations().entrySet()) {
                String lineNumber = item.getKey();
                List<String> stationsList = new ArrayList<>(item.getValue());
                stationsObject.put(lineNumber, stationsList);
                // ���������� ������� ����� ������� ������ �������
//                int count = item.getValue().size();
                // ��������� ���������� ������� �� ���� ������
//                totalCount += count;
            }
//            System.out.println("����� ���������� ������� (��� ����� ������) � ���������� �����: " + totalCount);

            // � ������� ������ ��������� ������ � ������� ����� � ������� ������� � �������� ��������,
            // ���� - ������ "stations"
            jo.put("stations", stationsObject);

            // �������� ������ � ������� �������� ������ � ���� ������
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jo);

            // ���������� ������ � ���� map.json, ��� ���������� � ��������� ������� writer
            PrintWriter writer = new PrintWriter(pathMapJson);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ����� �������� � ������ stations.json
    public static void createStationsJson() {
        // �������� �� WebParser stationIndex � ������� � �������� (���, �����, ���� ��������, ������� � ������� ��������)
        StationIndex stationIndex = WebParser.getStationIndex();

        // ������� ������� ������ ��� ������ ����� stations.json
        JSONObject stationsDataObject = new JSONObject();
        // ������� ������ ��������-������� �� ����� ������
        JSONArray jsonArrayStations = new JSONArray();

        try {
            // ������� map � ������� � �������� �� WebParser ������ parseStations
            for (Map.Entry<String, List<String>> item : WebParser.parseStations().entrySet()) {
                List<String> stationsList = new ArrayList<>(item.getValue());
                // �������� �� ������ ������� - ��� ����� ������ ����� �������
                for (String station : stationsList) {
                    // ������� ��������� ���-��� ��� ������ ���������� � ������� � ����������� ������� (�� ������� ����������)
                    LinkedHashMap<String, Object> objectLinkedHashMap = new LinkedHashMap<>();

                    objectLinkedHashMap.put("name", stationIndex.getStation(station).getName());
                    objectLinkedHashMap.put("line", stationIndex.getStation(station).getLine().getName());
                    objectLinkedHashMap.put("date", stationIndex.getStation(station).getDate());
                    if (stationIndex.getStation(station).getDepth() != null) {
                        objectLinkedHashMap.put("depth", stationIndex.getStation(station).getDepth());
                    }
                    objectLinkedHashMap.put("hasConnection", stationIndex.getStation(station).isHasConnection());

                    // ��������� ������ � ������� � ������ ��������-�������
                    jsonArrayStations.add(objectLinkedHashMap);
                }
            }
            // ��������� ������ � ������� ������
            stationsDataObject.put("stations", jsonArrayStations);

            // �������� ������ � "��������� ����" ������
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(stationsDataObject);

            // ���������� ��������� ������ � ���� stations.json
            PrintWriter writer = new PrintWriter(pathStationsJson);
            writer.write(json);
            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

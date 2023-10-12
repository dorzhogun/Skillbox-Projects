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

    // передаем в метод данные из WebParser
    // метод создания и записи map.json
    public static void createMapJson() {
        try {

            // создаем главный объект схемы метро для map.json
            JSONObject jo = new JSONObject();
            // создаем объект-массив ключ - номер линии, значение - имя линии
            JSONArray lineNumberAndName = new JSONArray();
            // обходим map с данными о линиях из класа WebParser
            // в объект-массив с данными о линии добавляем объекты с ключом - number/name, значением - номер/имя линии
            WebParser.parseLines().forEach((key, value) -> {
                JSONObject linesInfo = new JSONObject();
                String nu = "number";
                String na = "name";
                linesInfo.put(nu, key);
                linesInfo.put(na, value);
                lineNumberAndName.add(linesInfo);
            });

            // в главный объект кладем в качестве значения объект-массив с данными о линиях,
            // ключ - строка "lines"
            jo.put("lines", lineNumberAndName);

            // обходим map с данными о станциях из WebParser метода parseStations
            // сохраняем в объекте stationsObject ключ - номер линии, значение - список станций этой линии
            JSONObject stationsObject = new JSONObject();
            for (Map.Entry<String, List<String>> item : WebParser.parseStations().entrySet()) {
                String lineNumber = item.getKey();
                List<String> stationsList = new ArrayList<>(item.getValue());
                stationsObject.put(lineNumber, stationsList);
                // количество станций равна размеру списка станций
//                int count = item.getValue().size();
                // суммируем количества станций на всех линиях
//                totalCount += count;
            }
//            System.out.println("Общее количество станций (без учета дублей) в Московском метро: " + totalCount);

            // в главный объект сохраняем объект с номером линии и списком станций в качестве значения,
            // ключ - строка "stations"
            jo.put("stations", stationsObject);

            // Приводим объект к формату красивой печати в виде строки
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jo);

            // Записываем строку в файл map.json, все сбрасываем и закрываем процесс writer
            PrintWriter writer = new PrintWriter(pathMapJson);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // метод создания и записи stations.json
    public static void createStationsJson() {
        // вызываем из WebParser stationIndex с данными о станциях (имя, линия, дата открытия, глубина и наличии перехода)
        StationIndex stationIndex = WebParser.getStationIndex();

        // создаем главный объект для записи файла stations.json
        JSONObject stationsDataObject = new JSONObject();
        // создаем массив объектов-станций со всеми полями
        JSONArray jsonArrayStations = new JSONArray();

        try {
            // обходим map с данными о станциях из WebParser метода parseStations
            for (Map.Entry<String, List<String>> item : WebParser.parseStations().entrySet()) {
                List<String> stationsList = new ArrayList<>(item.getValue());
                // проходим по списку станций - нам нужны только имена станции
                for (String station : stationsList) {
                    // создаем связанную хэш-мап для вывода информации о станции в необходимом формате (по очереди добавления)
                    LinkedHashMap<String, Object> objectLinkedHashMap = new LinkedHashMap<>();

                    objectLinkedHashMap.put("name", stationIndex.getStation(station).getName());
                    objectLinkedHashMap.put("line", stationIndex.getStation(station).getLine().getName());
                    objectLinkedHashMap.put("date", stationIndex.getStation(station).getDate());
                    if (stationIndex.getStation(station).getDepth() != null) {
                        objectLinkedHashMap.put("depth", stationIndex.getStation(station).getDepth());
                    }
                    objectLinkedHashMap.put("hasConnection", stationIndex.getStation(station).isHasConnection());

                    // добавляем данные о станции в массив объектов-станций
                    jsonArrayStations.add(objectLinkedHashMap);
                }
            }
            // добавляем массив в главный объект
            stationsDataObject.put("stations", jsonArrayStations);

            // приводим строку к "красивому виду" печати
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(stationsDataObject);

            // Записываем финальную строку в файл stations.json
            PrintWriter writer = new PrintWriter(pathStationsJson);
            writer.write(json);
            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

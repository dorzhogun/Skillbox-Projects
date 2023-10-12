package core;

public class Station implements Comparable<Station> {
    private final Line line;
    private final String name;
    private String openingDate;
    private Double depth;
    private boolean hasConnection;


    public Station(String name, Line line, String openingDate) {
        this.name = name;
        this.line = line;
        this.openingDate = openingDate;
    }

    public Station(String name, Line line, String openingDate, Double depth) {
        this.name = name;
        this.line = line;
        this.openingDate = openingDate;
        this.depth = depth;
    }

    public Station(String name, Line line, String openingDate, Double depth, boolean hasConnection) {
        this.name = name;
        this.line = line;
        this.openingDate = openingDate;
        this.depth = depth;
        this.hasConnection = hasConnection;
    }

    public String getDate() {
        return this.openingDate;
    }

    public void setDate(String date) {
        this.openingDate = date;
    }

    public Double getDepth() {
        return this.depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public boolean isHasConnection() {
        return this.hasConnection;
    }

    public void setHasConnection(boolean hasConnection) {
        this.hasConnection = hasConnection;
    }

    public Station(String name, Line line) {
        this.name = name;
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Station station) {
        int lineComparison = line.compareTo(station.getLine());
        if (lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Station) obj) == 0;
    }

    @Override
    public String toString() {
        return "Название станции: " + name + ", номер линии: " + line.getNumber();
    }
}
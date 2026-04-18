
package metrozone1;

public class Interchange {
    private String station;
    private String fromLine;
    private String toLine;
    private double time;

    public Interchange(String station, String fromLine, String toLine, double time) {
        this.station = station;
        this.fromLine = fromLine;
        this.toLine = toLine;
        this.time = time;
    }

    public String getStation() {
        return station;
    }

    public String getFromLine() {
        return fromLine;
    }

    public String getToLine() {
        return toLine;
    }

    public double getTime() {
        return time;
    }
    
    @Override
    public String toString(){
        return station + ": " + fromLine + " -> " + toLine + " (+" + time + " mins";
    }
}

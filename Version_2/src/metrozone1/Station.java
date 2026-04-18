
package metrozone1;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private String name;
    private List<String> lines;

    public Station(String name) {
        this.name = name;
        this.lines = new ArrayList<>();
    }
    
    public void addLine(String line){
        lines.add(line);
    }

    public String getName() {
        return name;
    }

    public List<String> getLines() {
        return lines;
    }
    
    @Override
    public String toString() {
        return name + " (lines: " + String.join(", ", lines) + ")";
    }

}

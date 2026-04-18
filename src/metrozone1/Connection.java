
package metrozone1;

public class Connection {
    private String start;
    private String end;
    private double time;
    private String line;
    private String direction;
    private boolean closed = false;
    private double delay = 0;

    public Connection(String start, String end, double time, String line, String direction) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.line = line;
        this.direction = direction;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public double getTime() {
        return time;
    }

    public String getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isClosed() {
        return closed;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }
    
    
    public double getTotalTime(){
        return closed ? Double.POSITIVE_INFINITY : time + delay;
    }
    
      public void close() {
        closed = true;
    }

    public void open() {
        closed = false;
    }

    
    @Override
    public String toString() {
        String status = closed ? " [CLOSED]" : (delay > 0 ? " [DELAY: +" + delay + " min]" : "");
        return line + " (" + direction + "): " + start + " -> " + end + " " + time + " min" + status;
}
    
            
}

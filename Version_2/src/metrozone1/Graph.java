package metrozone1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Graph {

    private List<Station> stations = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private List<Interchange> interchanges = new ArrayList<>();

    public void addStation(Station s) {
        stations.add(s);
    }

    public void addConnection(Connection c) {
        connections.add(c);
    }

    public void addInterchange(Interchange i) {
        interchanges.add(i);
    }

    //  LOAD DATA
    public void loadConnections(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int lineNumber = 0;
        br.readLine(); // skip header
        lineNumber++;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            if (line.trim().isEmpty()) {
                System.out.println("Empty line in file Connections: " + lineNumber);
                continue;
            }

            String[] p = line.split(",");

            if (p.length < 5) {
                System.out.println("Format error in file Connections on line " + lineNumber);
                continue;
            }

            String start = p[0].trim();
            String end = p[1].trim();
            double time = Double.parseDouble(p[2].trim());
            String lineName = p[3].trim();
            String direction = p[4].trim();

            addConnection(new Connection(start, end, time, lineName, direction));
        }
        br.close();
    }

    public void loadInterchanges(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] p = line.split(",");
            String station = p[0].trim();
            String fromLine = p[1].trim();
            String toLine = p[2].trim();
            double time = Double.parseDouble(p[3].trim());

            addInterchange(new Interchange(station, fromLine, toLine, time));
        }
        br.close();
    }

    //  ENGINEER OPERATIONS
    public void closeTrack(String start, String end) {
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(start)
                    && c.getEnd().equalsIgnoreCase(end)) {
                c.close();
                System.out.println("Track closed: " + start + " -> " + end);
            }
        }
    }

    public void openTrack(String start, String end) {
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(start)
                    && c.getEnd().equalsIgnoreCase(end)) {
                c.open();
                System.out.println("Track opened: " + start + " -> " + end);
            }
        }
    }

    public void addDelay(String start, String end, double minutes) {
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(start)
                    && c.getEnd().equalsIgnoreCase(end)) {
                c.setDelay(minutes);
                System.out.println("Delay added: " + start + " -> " + end);
            }
        }
    }

    public void removeDelay(String start, String end) {
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(start)
                    && c.getEnd().equalsIgnoreCase(end)) {
                c.setDelay(0);
                System.out.println("Delay removed: " + start + " -> " + end);
            }
        }
    }

    public void printClosedTracks() {
        System.out.println("\nClosed tracks:");
        boolean found = false;
        for (Connection c : connections) {
            if (c.isClosed()) {
                System.out.println(" - " + c.getLine()
                        + "(" + c.getDirection() + "): "
                        + c.getStart() + " -> " + c.getEnd());
                found = true;
            }
        }
        if (!found) {
            System.out.println("All tracks are open");
        }
    }

    public void printDelayedTracks() {
        System.out.println("\nDelayed tracks:");
        boolean found = false;
        for (Connection c : connections) {
            if (c.getDelay() > 0) {
                System.out.println(" - " + c.getLine()
                        + "(" + c.getDirection() + "): "
                        + c.getStart() + " -> " + c.getEnd()
                        + " | Normal: " + c.getTime() + " min"
                        + " | Delayed: " + c.getTotalTime() + " min");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No delays");
        }
    }

    //  STATION INFO
    public List<String> getAllLines() {
        List<String> lines = new ArrayList<>();
        for (Connection c : connections) {
            if (!lines.contains(c.getLine())) {
                lines.add(c.getLine());
            }
        }
        return lines;
    }

    public void printStationByLine(String lineName) {
        System.out.println("\nStations on " + lineName + ":");
        List<String> seen = new ArrayList<>();
        for (Connection c : connections) {
            if (c.getLine().equalsIgnoreCase(lineName)
                    && !seen.contains(c.getStart())) {
                seen.add(c.getStart());
                System.out.println(" - " + c.getStart());
            }
        }
    }

    public void printStationInfo(String name) {
        System.out.println("\nStation: " + name);
        boolean found = false;
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(name)) {
                System.out.println(" - " + c.getLine()
                        + "(" + c.getDirection() + ")"
                        + ": to " + c.getEnd()
                        + " " + c.getTime() + " min"
                        + (c.isClosed() ? " [CLOSED]" : "")
                        + (c.getDelay() > 0 ? " [DELAY: +" + c.getDelay() + " min]" : ""));
                found = true;
            }
        }
        if (!found) {
            System.out.println("Station not found: " + name);
        }
    }

    public List<Connection> getConnectionsFromStation(String name) {
        List<Connection> result = new ArrayList<>();
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(name)) {
                result.add(c);
            }
        }
        return result;
    }
    //  FIND ROUTE (Dijkstra)

    public void findRoute(String start, String end) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        //init map of all possible nodes
        initialiseNodes(dist, prev);

        // start point for station
        if (!setStartNodes(dist, start)) {
            System.out.println("Start station " + start + " not found!");
            return;
        }
        runDijkstra(dist, prev, end);//start of Dijkstra
        printResult(dist, prev, start, end); // search of the best resoult and print
    }

    private void runDijkstra(Map<String, Double> dist, Map<String, String> prev, String end) {
        PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.POSITIVE_INFINITY)));
        // add all start nodes where tine is 0.0 to the queue
        dist.entrySet().stream()
                .filter(e -> e.getValue() == 0.0)
                .forEach(e -> pq.add(e.getKey()));

        // Dijkstra main loop
        while (!pq.isEmpty()) {
            String current = pq.poll();
            String[] parts = current.split("\\|");
            String currentStation = parts[0];
            String currentLine = parts[1];
            String currentDirection = parts[2];

            if (currentStation.equalsIgnoreCase(end.trim())) {
                break;
            }
            for (Connection c : connections) {
                if (c.getStart().equalsIgnoreCase(currentStation)
                        && c.getLine().equalsIgnoreCase(currentLine)
                        && c.getDirection().equalsIgnoreCase(currentDirection)) {
                    String neighbour = c.getEnd() + "|" + c.getLine() + "|" + c.getDirection();
                    double newDist = dist.get(current) + c.getTotalTime();

                    if (newDist < dist.getOrDefault(neighbour, Double.POSITIVE_INFINITY)) {
                        dist.put(neighbour, newDist);
                        prev.put(neighbour, current);
                        pq.add(neighbour);
                    }
                }
            }
            // make interchange
            for (Interchange i : interchanges) {
                if (i.getStation().equalsIgnoreCase(currentStation)
                        && i.getFromLine().equalsIgnoreCase(currentLine)) {
                    for (Connection c : connections) {
                        if (c.getStart().equalsIgnoreCase(currentStation)
                                && c.getLine().equalsIgnoreCase(i.getToLine())) {

                            String neighbor = i.getStation() + "|" + i.getToLine() + "|" + c.getDirection();
                            double newDist = dist.get(current) + i.getTime();

                            if (newDist < dist.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                                dist.put(neighbor, newDist);
                                prev.put(neighbor, current);
                                pq.add(neighbor); // priority queue
                            }
                        }
                    }
                }
            }
        }
    }

    private void printResult(Map<String, Double> dist, Map<String, String> prev, String start, String end) {
        String bestEnd = null;
        double bestTime = Double.POSITIVE_INFINITY;

        for (String node : dist.keySet()) {
            String stationName = node.split("\\|")[0];
            if (stationName.equalsIgnoreCase(end.trim()) && dist.get(node) < bestTime) {
                bestTime = dist.get(node);
                bestEnd = node;
            }
        }
        
        if (bestEnd == null || bestTime == Double.POSITIVE_INFINITY) {
            System.out.println("Route not found between " + start + " and " + end);
            return;
        }
        // Recover path
        List<String> path = new ArrayList<>();
        String step = bestEnd;
        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }
        Collections.reverse(path);
        System.out.println("\nRoute: " + start + " to " + end + ":");
        int stepNum = 1;
        String[] startInfo = path.get(0).split("\\|");
        System.out.println("(" + stepNum++ + ") Start: " + start + " on " 
                + startInfo[1] + " (" + startInfo[2] + ")");
        for (int i = 1; i < path.size(); i++) {
            String[] cur = path.get(i).split("\\|");
            String[] prv = path.get(i - 1).split("\\|");
            
            if (prv[0].equalsIgnoreCase(cur[0])) {
                // Interchange
                System.out.printf("(%d) Change: %s %s -> %s (%.2f min)%n", 
                        stepNum++, cur[0], prv[1], cur[1], (dist.get(path.get(i)) - dist.get(path.get(i-1))));
            } else {
                // between stations
                System.out.printf("(%d) %s (%s): %s -> %s (%.2f min)%n", 
                        stepNum++, cur[1], cur[2], prv[0], cur[0],
                        getConnectionTime(prv[0], cur[0], cur[1], cur[2]));
            }
        }
        System.out.printf("Total Journey Time: %.2f minutes%n", bestTime);
    }

    private void initialiseNodes(Map<String, Double> dist, Map<String, String> prev) {
        for (Connection c : connections) {
            String fromNode = c.getStart() + "|" + c.getLine() + "|" + c.getDirection();
            String toNode = c.getEnd() + "|" + c.getLine() + "|" + c.getDirection();
            dist.putIfAbsent(fromNode, Double.POSITIVE_INFINITY);
            dist.putIfAbsent(toNode, Double.POSITIVE_INFINITY);
            prev.putIfAbsent(fromNode, null);
            prev.putIfAbsent(toNode, null);
        }
        //init og interchanges
        for (Interchange i : interchanges) {
            for (Connection c : connections) {
                if (c.getLine().equalsIgnoreCase(i.getFromLine())
                        && c.getStart().equalsIgnoreCase(i.getStation())) {
                    String fromNode = i.getStation() + "|" + i.getFromLine() + "|" + c.getDirection();
                    dist.putIfAbsent(fromNode, Double.POSITIVE_INFINITY);
                    prev.putIfAbsent(fromNode, null);
                }
                if (c.getLine().equalsIgnoreCase(i.getToLine())
                        && c.getStart().equalsIgnoreCase(i.getStation())) {
                    String toNode = i.getStation() + "|" + i.getToLine() + "|" + c.getDirection();
                    dist.putIfAbsent(toNode, Double.POSITIVE_INFINITY);
                    prev.putIfAbsent(toNode, null);
                }
            }
        }
    }

    private boolean setStartNodes(Map<String, Double> dist, String start) {
        boolean found = false;
        for (String node : dist.keySet()) {
            String stationName = node.split("\\|")[0].trim();
            if (stationName.equalsIgnoreCase(start.trim())) {
                dist.put(node, 0.0);
                found = true;
            }
        }
        return found;
    }
    // logic of Dijkstra

    private double getConnectionTime(String from, String to, String line, String direction) {
        for (Connection c : connections) {
            if (c.getStart().equalsIgnoreCase(from)
                    && c.getEnd().equalsIgnoreCase(to)
                    && c.getLine().equalsIgnoreCase(line)
                    && c.getDirection().equalsIgnoreCase(direction)) {
                return c.getTotalTime();
            }
        }
        return 0.0;
    }
}

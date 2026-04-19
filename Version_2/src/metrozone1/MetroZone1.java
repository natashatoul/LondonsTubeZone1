package metrozone1;

import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class MetroZone1 {
    
    private MetroListener listener;
    private Graph graph;
    private Scanner sc;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MetroGui().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setMetroListener(MetroListener listener) {
        this.listener = listener;
    }

    public MetroZone1() throws Exception {
        graph = new Graph();
        sc = new Scanner(System.in);
        graph.loadConnections("data/Connections.csv");
        graph.loadInterchanges("data/Interchanges.csv");
        System.out.println("Loaded data");
    }

    // ── Customer actions (GUI calls these) ────────────────────────

    public void loadAllLines() {
        List<String> lines = graph.getAllLines();
        if (lines.isEmpty()) {
            if (listener != null) listener.onError("No lines found.");
            return;
        }
        if (listener != null) listener.onLinesLoaded(lines);
    }

    public void loadAllStations() {
        List<String> stations = graph.getAllStations();
        if (listener != null) listener.onStationsLoaded(stations);
    }

    public void searchByLine(String lineName) {
        if (lineName == null || lineName.isEmpty()) {
            if (listener != null) listener.onError("Please select a line.");
            return;
        }
        String result = graph.getStationsByLine(lineName);
        if (listener != null) listener.onStationsByLineFound(lineName, result);
    }

    public void searchByStation(String stationName) {
        if (stationName == null || stationName.trim().isEmpty()) {
            if (listener != null) listener.onError("Station name cannot be empty.");
            return;
        }
        String result = graph.getStationInfo(stationName);
        if (listener != null) listener.onStationInfoFound(result);
    }

    public void planJourney(String start, String end) {
        if (start == null || start.trim().isEmpty()
                || end == null || end.trim().isEmpty()) {
            if (listener != null) listener.onError("Station names cannot be empty.");
            return;
        }
        String result = graph.getRoute(start, end);
        if (listener != null) listener.onRouteFound(result);
    }

    // ── Engineer actions (GUI calls these) ────────────────────────

    public void loadClosedTracks() {
        String result = graph.getClosedTracks();
        if (listener != null) listener.onClosedTracksLoaded(result);
    }

    public void loadDelayedTracks() {
        String result = graph.getDelayedTracks();
        if (listener != null) listener.onDelayedTracksLoaded(result);
    }

    public void loadConnectionsFromStation(String stationName) {
        if (stationName == null || stationName.trim().isEmpty()) {
            if (listener != null) listener.onError("Station name cannot be empty.");
            return;
        }
        List<Connection> conns = graph.getConnectionsFromStation(stationName);
        if (conns.isEmpty()) {
            if (listener != null)
                listener.onError("No connections found for: " + stationName);
            return;
        }
        if (listener != null) listener.onConnectionsLoaded(conns);
    }

    public void toggleConnectionStatus(Connection selected) {
        if (selected.isClosed()) {
            selected.open();
            if (listener != null)
                listener.onConnectionStatusChanged("Track OPENED: "
                        + selected.getStart() + " → " + selected.getEnd());
        } else {
            selected.close();
            if (listener != null)
                listener.onConnectionStatusChanged("Track CLOSED: "
                        + selected.getStart() + " → " + selected.getEnd());
        }
    }

    public void setDelay(Connection selected, double delay) {
        if (delay <= 0) {
            if (listener != null)
                listener.onError("Delay must be greater than 0.");
            return;
        }
        selected.setDelay(delay);
        if (listener != null)
            listener.onDelaySet("Delay of " + delay + " min added to: "
                    + selected.getStart() + " → " + selected.getEnd());
    }

    public void removeDelay(Connection selected) {
        selected.setDelay(0);
        if (listener != null)
            listener.onDelayRemoved("Delay removed from: "
                    + selected.getStart() + " → " + selected.getEnd());
    }

    // ── Original console methods — kept so console still works ────

    private void run() {
        while (true) {
            System.out.println("1. Customer Menu");
            System.out.println("2. Engineer Menu");
            System.out.println("3. Exit");
            System.out.println("Select option: ");

            int choice = readInt();
            switch (choice) {
                case 1: customerMenu(); break;
                case 2: engineerMenu(); break;
                case 3:
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3");
            }
        }
    }

    private void customerMenu() {
        while (true) {
            System.out.println("1. Search Station");
            System.out.println("2. Journey Planner");
            System.out.println("3. Go Back");
            System.out.println("Select option: ");

            int choice = readInt();
            switch (choice) {
                case 1: searchStationMenu(); break;
                case 2: journeyPlanner(); break;
                case 3: return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3");
            }
        }
    }

    private void searchStationMenu() {
        while (true) {
            System.out.println("1. Search by Line");
            System.out.println("2. Search by Station");
            System.out.println("3. Go Back");

            int choice = readInt();
            switch (choice) {
                case 1: searchByLineConsole(); break;
                case 2: searchByStationConsole(); break;
                case 3: return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3");
            }
        }
    }

    private void searchByLineConsole() {
        List<String> lines = graph.getAllLines();
        System.out.println("\nAvailable lines: ");
        for (int i = 0; i < lines.size(); i++) {
            System.out.println((i + 1) + ". " + lines.get(i));
        }
        System.out.println("Select line number: ");
        int choice = readInt();
        if (choice < 1 || choice > lines.size()) {
            System.out.println("Invalid selection");
            return;
        }
        graph.printStationByLine(lines.get(choice - 1));
        pressAnyKey();
    }

    private void searchByStationConsole() {
        System.out.println("Enter station name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Station name cannot be empty");
            return;
        }
        graph.printStationInfo(name);
        pressAnyKey();
    }

    private void journeyPlanner() {
        System.out.println("\nStart Station: ");
        String start = sc.nextLine().trim();
        if (start.isEmpty()) {
            System.out.println("Station name cannot be empty");
            return;
        }
        System.out.println("End station: ");
        String end = sc.nextLine().trim();
        if (end.isEmpty()) {
            System.out.println("Station name cannot be empty");
            return;
        }
        graph.findRoute(start, end);
        pressAnyKey();
    }

    private void engineerMenu() {
        while (true) {
            System.out.println("1. View Disruptions");
            System.out.println("2. Manage Connections");
            System.out.println("3. Go Back");

            int choice = readInt();
            switch (choice) {
                case 1: viewDisruptions(); break;
                case 2: manageConnections(); break;
                case 3: return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3");
            }
        }
    }

    private void viewDisruptions() {
        while (true) {
            System.out.println("1. Track Closures");
            System.out.println("2. Delays");
            System.out.println("3. Go Back");

            int choice = readInt();
            switch (choice) {
                case 1: graph.printClosedTracks(); pressAnyKey(); break;
                case 2: graph.printDelayedTracks(); pressAnyKey(); break;
                case 3: return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3");
            }
        }
    }

    private void manageConnections() {
        System.out.println("\nEnter station name: ");
        String stationName = sc.nextLine().trim();
        if (stationName.isEmpty()) {
            System.out.println("Station name cannot be empty");
            return;
        }
        List<Connection> conns = graph.getConnectionsFromStation(stationName);
        if (conns.isEmpty()) {
            System.out.println("No connections found for " + stationName);
            return;
        }
        for (int i = 0; i < conns.size(); i++) {
            Connection c = conns.get(i);
            System.out.println((i + 1) + ". " + c.getLine() + "(" + c.getDirection() + ") "
                    + c.getStart() + " to " + c.getEnd());
        }
        int idx = readInt();
        if (idx < 1 || idx > conns.size()) {
            System.out.println("Invalid selection");
            return;
        }
        manageSelectedConnection(conns.get(idx - 1));
    }

    private void manageSelectedConnection(Connection selected) {
        while (true) {
            System.out.println("Selected: " + selected.getLine()
                    + "(" + selected.getDirection() + ") "
                    + selected.getStart() + " to " + selected.getEnd());
            System.out.println("1. Set Status (Open/Close)");
            System.out.println("2. Set Delay");
            System.out.println("3. Remove Delay");
            System.out.println("4. Go Back");

            int choice = readInt();
            switch (choice) {
                case 1: toggleConnectionStatus(selected); break;
                case 2:
                    System.out.println("Enter delay in minutes: ");
                    double delay = readDouble();
                    setDelay(selected, delay);
                    break;
                case 3: removeDelay(selected); break;
                case 4: return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private int readInt() {
        try {
            int val = sc.nextInt();
            sc.nextLine();
            return val;
        } catch (java.util.InputMismatchException e) {
            sc.nextLine();
            System.out.println("Please enter a valid number");
            return -1;
        }
    }

    private double readDouble() {
        try {
            double val = sc.nextDouble();
            sc.nextLine();
            return val;
        } catch (java.util.InputMismatchException e) {
            sc.nextLine();
            System.out.println("Please enter a valid number");
            return 0.0;
        }
    }

    private void pressAnyKey() {
        System.out.println("\nPress 'Enter' to continue");
        sc.nextLine();
    }
}

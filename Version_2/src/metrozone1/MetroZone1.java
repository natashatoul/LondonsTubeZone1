
package metrozone1;

//import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MetroZone1 {
    private Graph graph;
    private Scanner sc;
    public static void main(String[] args) throws Exception {
        new MetroZone1().run();
    }
    
    public MetroZone1() throws Exception {
            
        graph = new Graph();
        sc = new Scanner(System.in);
        
        graph.loadConnections("data/Connections.csv");
        graph.loadInterchanges("data/Interchanges.csv");
        System.out.println("Loaded data");   
    }

    private void run() {
        while (true) {
            System.out.println("1. Customer Menu");
            System.out.println("2. Engineer Menu");
            System.out.println("3. Exit");
            System.out.println("Select option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1: customerMenu();
                break;
                case 2: engineerMenu();
                break;
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
                case 1: searchStationMenu();
                break;
                case 2: journeyPlanner();
                break;
                case 3:
                return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3"); 
            }
        }
    }
    private void searchStationMenu(){
        while (true) {
            System.out.println("1. Search by Line");
            System.out.println("2. Search by Station");
            System.out.println("3. Go Back");
            System.out.println("Select option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1: searchByLine();
                break;
                case 2: searchByStation();
                break;
                case 3:
                return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3"); 
            }
            
        }
    }
    private void searchByLine(){
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
    private void searchByStation(){
        System.out.println("Enter station name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Station name cannot be empty");
            return;
        }
        graph.printStationInfo(name);
        pressAnyKey();
    }

    private void journeyPlanner(){
        System.out.println("\nStart Station: ");
        String start = sc.nextLine().trim();
        if (start.isEmpty()){
            System.out.println("Station name cannot be empty");
            return;
        }
        System.out.println("End station: ");
        String end = sc.nextLine().trim();
        if (end.isEmpty()){
            System.out.println("Station name cannot be empty");
            return;
        }
        graph.findRoute(start, end);
        pressAnyKey();
    }
    // engineer menu
    private void engineerMenu() {
         while (true) {
            System.out.println("1. View Disruptions");
            System.out.println("2. Manage Connections");
            System.out.println("3. Go Back");
            System.out.println("Select option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1: viewDisruptions();
                break;
                case 2: manageConnections();
                break;
                case 3:
                return;
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
            System.out.println("Select option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1: graph.printClosedTracks();
                pressAnyKey();
                break;
                case 2: graph.printDelayedTracks();
                pressAnyKey();
                break;
                case 3:
                return;
                default:
                    System.out.println("Invalid input. Enter 1, 2 or 3"); 
            }  
        }
    }
    private void manageConnections(){
        System.out.println("\nEnter ststion name: ");
        String stationName = sc.nextLine().trim();
        if (stationName.isEmpty()){
            System.out.println("Station name cannot be empty");
            return;
        }
        List<Connection> conns = graph.getConnectionsFromStation(stationName);
        if (conns.isEmpty()){
            System.out.println("No connections found for " + stationName);
            return;
        }
        
        //Connections - list
        System.out.println("\nSelect connection: ");
        for (int i = 0; i < conns.size(); i++) {
            Connection c = conns.get(i);
            System.out.println((i + 1) + ". " + c.getLine() + "(" + c.getDirection() + ")" + 
                    c.getStart() + " to " + c.getEnd());
        }
        System.out.println("Select: ");
        int idx = readInt();
        if (idx < 1 || idx > conns.size()){
            System.out.println("Invalid selection");
            return;
        }
        Connection selected = conns.get(idx - 1);
        manageSelectedConnection(selected);
    }
    private void manageSelectedConnection(Connection selected){
        while (true) {
            System.out.println("Selected: " + selected.getLine() + "(" + selected.getDirection()
            + ")" + selected.getStart() + " to " + selected.getEnd());
            System.out.println("Status: " + (selected.isClosed() ? "CLOSED" : "OPEN")
                    + (selected.getDelay() > 0
                    ? " | Delay: +" + selected.getDelay() + " min" : ""));
            System.out.println("1. Set Status (Open/Clise)");
            System.out.println("2. Set Delay");
            System.out.println("3. Remove Delay");
            System.out.println("4. Go Back");
            System.out.println("Select option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1:
                    if (selected.isClosed()) {
                        selected.open();
                        System.out.println("Track opened: " + selected.getStart() + " -> " + selected.getEnd());
                    } else {
                        selected.close();
                        System.out.println("Track closed: " + selected.getStart() + " -> " + selected.getEnd());
                    }
                    break;
                case 2:
                    System.out.println("Enter delay in minutes: ");
                    double delay = readDouble();
                    if (delay <= 0) {
                        System.out.println("Delay must be greater than 0");
                        break;
                    }
                    selected.setDelay(delay);
                    System.out.println("Delay of " + delay + " min added");
                    break;
                case 3:
                    selected.setDelay(0);
                    System.out.println("Delay removed");
                    break;
                case 4: return;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, 3 or 4");
            }
        }
    }
    
    // methods helpers
    private int readInt(){
        try {
            int val = sc.nextInt();
            sc.nextLine();
            return val;
        } catch (InputMismatchException e) {
            sc.nextLine();
            System.out.println("Please enter a valid number");
            return -1;
        }
    }
    private double readDouble(){
        try {
            double val = sc.nextDouble();
            sc.nextLine();
            return val;
        } catch (InputMismatchException e) {
            sc.nextLine();
            System.out.println("Please enter a valid number");
            return 0.0;
        }
    }
    private void pressAnyKey(){
        System.out.println("\nPress 'Enter' to continue");
        sc.nextLine();
    }
    
}
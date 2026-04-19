package metrozone1;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

//Bring the Code to life with a GUI context. This contains all of the code for the GUI itself, its layout, design etc

public class MetroGui extends JFrame implements MetroListener {

    private MetroZone1 controller;
    private List<Connection> currentConnections;

    private static final Color DARK_BLUE = new Color(175, 175, 225);
    private static final Color PURPLE_LIGHT = new Color(175, 175, 255);
    private static final Color TEXT_COLOR  = new Color(65, 65, 65);

    // ── Components ────────────────────────────────────────────────
    private JTextArea resultTextArea;
    private JLabel statusLabel;
    private JComboBox<String> lineComboBox;
    private JComboBox<String> stationComboBox;
    private JComboBox<String> startComboBox;
    private JComboBox<String> endComboBox;
    private JTextField stationSearchField;
    private JTextField startField;
    private JTextField endField;
    private JTextField engineerStationField;
    private JList<String> connectionsList;
    private DefaultListModel<String> connectionsListModel;
    private JTextField delayField;

    // ── Constructor ───────────────────────────────────────────────
    public MetroGui() {
        buildGui();
        try {
            controller = new MetroZone1();
            controller.setMetroListener(this);
            controller.loadAllLines();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    // ── Build GUI ─────────────────────────────────────────────────
    private void buildGui() {
        setTitle("Metro Zone 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BLUE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(PURPLE_LIGHT);
        tabs.setForeground(TEXT_COLOR);
        tabs.addTab("Customer", buildCustomerPanel());
        tabs.addTab("Engineer", buildEngineerPanel());

        // Results area
        resultTextArea = new JTextArea(10, 50);
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultTextArea.setBackground(PURPLE_LIGHT);
        resultTextArea.setForeground(TEXT_COLOR);
        resultTextArea.setCaretColor(TEXT_COLOR);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultTextArea);
        TitledBorder resultBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR), "Results"
        );
        resultBorder.setTitleColor(TEXT_COLOR);
        resultScroll.setBorder(resultBorder);
        resultScroll.getViewport().setBackground(PURPLE_LIGHT);

        // Status label
        statusLabel = new JLabel(" Ready");
        statusLabel.setForeground(new Color(200, 180, 255));
        statusLabel.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBackground(DARK_BLUE);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.add(tabs, BorderLayout.NORTH);
        main.add(resultScroll, BorderLayout.CENTER);
        main.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(main);
    }

    // ── Customer Panel ────────────────────────────────────────────
    private JPanel buildCustomerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BLUE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search by Line
        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linePanel.setBackground(PURPLE_LIGHT);
        linePanel.setBorder(createPurpleBorder("Search by Line"));
        lineComboBox = new JComboBox<>();
        styleComboBox(lineComboBox);
        JButton searchByLineBtn = createButton("Search");
        searchByLineBtn.addActionListener(e -> {
            if (lineComboBox.getSelectedItem() != null)
                controller.searchByLine(lineComboBox.getSelectedItem().toString());
        });
        linePanel.add(styledLabel("Line:"));
        linePanel.add(lineComboBox);
        linePanel.add(searchByLineBtn);

        // Search by Station
        JPanel stationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stationPanel.setBackground(PURPLE_LIGHT);
        stationPanel.setBorder(createPurpleBorder("Search by Station"));
        stationSearchField = new JTextField(15);
        stationSearchField.setVisible(false);
        stationComboBox = new JComboBox<>();
        stationComboBox.setPrototypeDisplayValue("                    ");
        styleComboBox(stationComboBox);
        JButton searchStationBtn = createButton("Search");
        searchStationBtn.addActionListener(e -> {
            if (stationComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a station.");
                return;
            }
            controller.searchByStation(stationComboBox.getSelectedItem().toString());
        });
        stationPanel.add(styledLabel("Station:"));
        stationPanel.add(stationComboBox);
        stationPanel.add(searchStationBtn);

        // Journey Planner
        JPanel journeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        journeyPanel.setBackground(PURPLE_LIGHT);
        journeyPanel.setBorder(createPurpleBorder("Journey Planner"));
        startField = new JTextField(12);
        startField.setVisible(false);
        endField = new JTextField(12);
        endField.setVisible(false);
        startComboBox = new JComboBox<>();
        endComboBox = new JComboBox<>();
        startComboBox.setPrototypeDisplayValue("                    ");
        endComboBox.setPrototypeDisplayValue("                    ");
        styleComboBox(startComboBox);
        styleComboBox(endComboBox);
        JButton planBtn = createButton("Plan Journey");
        planBtn.setBackground(new Color(120, 0, 180));
        planBtn.addActionListener(e -> {
            if (startComboBox.getSelectedItem() == null
                    || endComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select both stations.");
                return;
            }
            controller.planJourney(
                startComboBox.getSelectedItem().toString(),
                endComboBox.getSelectedItem().toString()
            );
        });
        journeyPanel.add(styledLabel("From:"));
        journeyPanel.add(startComboBox);
        journeyPanel.add(styledLabel("To:"));
        journeyPanel.add(endComboBox);
        journeyPanel.add(planBtn);

        panel.add(linePanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(stationPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(journeyPanel);
        return panel;
    }

    // ── Engineer Panel ────────────────────────────────────────────
    private JPanel buildEngineerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BLUE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Disruptions
        JPanel disruptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        disruptionPanel.setBackground(PURPLE_LIGHT);
        disruptionPanel.setBorder(createPurpleBorder("View Disruptions"));
        JButton closedBtn = createButton("Closed Tracks");
        closedBtn.setBackground(new Color(160, 30, 30));
        closedBtn.addActionListener(e -> controller.loadClosedTracks());
        JButton delayedBtn = createButton("Delayed Tracks");
        delayedBtn.setBackground(new Color(180, 120, 0));
        delayedBtn.addActionListener(e -> controller.loadDelayedTracks());
        disruptionPanel.add(closedBtn);
        disruptionPanel.add(delayedBtn);

        // Manage Connections
        JPanel managePanel = new JPanel(new BorderLayout(5, 5));
        managePanel.setBackground(PURPLE_LIGHT);
        managePanel.setBorder(createPurpleBorder("Manage Connections"));

        JPanel stationRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stationRow.setBackground(PURPLE_LIGHT);
        engineerStationField = new JTextField(15);
        styleTextField(engineerStationField);
        JButton loadConnsBtn = createButton("Load Connections");
        loadConnsBtn.addActionListener(e ->
            controller.loadConnectionsFromStation(engineerStationField.getText())
        );
        stationRow.add(styledLabel("Station:"));
        stationRow.add(engineerStationField);
        stationRow.add(loadConnsBtn);

        connectionsListModel = new DefaultListModel<>();
        connectionsList = new JList<>(connectionsListModel);
        connectionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionsList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        connectionsList.setBackground(PURPLE_LIGHT);
        connectionsList.setForeground(TEXT_COLOR);
        connectionsList.setSelectionBackground(new Color(120, 0, 180));
        connectionsList.setSelectionForeground(Color.WHITE);
        JScrollPane listScroll = new JScrollPane(connectionsList);
        listScroll.setPreferredSize(new Dimension(400, 80));
        listScroll.getViewport().setBackground(PURPLE_LIGHT);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionRow.setBackground(PURPLE_LIGHT);
        JButton toggleBtn = createButton("Open / Close");
        toggleBtn.addActionListener(e -> {
            int idx = connectionsList.getSelectedIndex();
            if (idx == -1 || currentConnections == null) {
                JOptionPane.showMessageDialog(this, "Please select a connection first.");
                return;
            }
            controller.toggleConnectionStatus(currentConnections.get(idx));
        });
        delayField = new JTextField(5);
        styleTextField(delayField);
        JButton setDelayBtn = createButton("Set Delay (mins)");
        setDelayBtn.addActionListener(e -> {
            int idx = connectionsList.getSelectedIndex();
            if (idx == -1 || currentConnections == null) {
                JOptionPane.showMessageDialog(this, "Please select a connection first.");
                return;
            }
            try {
                double delay = Double.parseDouble(delayField.getText());
                controller.setDelay(currentConnections.get(idx), delay);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });
        JButton removeDelayBtn = createButton("Remove Delay");
        removeDelayBtn.setBackground(new Color(160, 30, 30));
        removeDelayBtn.addActionListener(e -> {
            int idx = connectionsList.getSelectedIndex();
            if (idx == -1 || currentConnections == null) {
                JOptionPane.showMessageDialog(this, "Please select a connection first.");
                return;
            }
            controller.removeDelay(currentConnections.get(idx));
        });
        actionRow.add(toggleBtn);
        actionRow.add(styledLabel("Delay:"));
        actionRow.add(delayField);
        actionRow.add(setDelayBtn);
        actionRow.add(removeDelayBtn);

        managePanel.add(stationRow, BorderLayout.NORTH);
        managePanel.add(listScroll, BorderLayout.CENTER);
        managePanel.add(actionRow, BorderLayout.SOUTH);

        panel.add(disruptionPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(managePanel);
        return panel;
    }

    // ── Style Helpers ─────────────────────────────────────────────
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(90, 0, 140));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setBackground(PURPLE_LIGHT);
        box.setForeground(TEXT_COLOR);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(PURPLE_LIGHT);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createLineBorder(new Color(150, 100, 200)));
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    private TitledBorder createPurpleBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 100, 200)), title
        );
        border.setTitleColor(TEXT_COLOR);
        return border;
    }

    // ── Interface Methods — Controller → GUI ──────────────────────

    @Override
    public void onLinesLoaded(List<String> lines) {
        lineComboBox.removeAllItems();
        for (String line : lines) lineComboBox.addItem(line);
        controller.loadAllStations();
        
    }

    @Override
    public void onStationsLoaded(List<String> stations) {
        startComboBox.removeAllItems();
        endComboBox.removeAllItems();
        stationComboBox.removeAllItems();
        for (String s : stations) {
            startComboBox.addItem(s);
            endComboBox.addItem(s);
            stationComboBox.addItem(s);
        }
    }

    @Override public void onStationsByLineFound(String line, String result) { resultTextArea.setText(result); }
    @Override public void onStationInfoFound(String result)                  { resultTextArea.setText(result); }
    @Override public void onRouteFound(String result)                        { resultTextArea.setText(result); }
    @Override public void onClosedTracksLoaded(String result)                { resultTextArea.setText(result); }
    @Override public void onDelayedTracksLoaded(String result)               { resultTextArea.setText(result); }

    @Override
    public void onConnectionsLoaded(List<Connection> connections) {
        this.currentConnections = connections;
        connectionsListModel.clear();
        for (Connection c : connections) {
            connectionsListModel.addElement(
                c.getLine() + " | " + c.getStart() + " → " + c.getEnd()
            );
        }
    }

    @Override public void onConnectionStatusChanged(String result) { statusLabel.setText(" " + result); }
    @Override public void onDelaySet(String result)                { statusLabel.setText(" " + result); }
    @Override public void onDelayRemoved(String result)            { statusLabel.setText(" " + result); }

    @Override
    public void onError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MetroGui().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

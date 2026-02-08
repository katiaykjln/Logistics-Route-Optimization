package OptimizeRoute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/*Description:
This is a comprehensive visualization tool for a delivery route optimization system.
It acts as both a graphical interface (view) and an interactive handler (controller),
connecting user input with the backend route data structure.
Main Features:
1. Visualizes doubly linked list nodes on a 2D map.
2. Implements the "nearest neighbor" greedy algorithm for route optimization.
3. Provides real-time interactive functions (click to view, add, and delete).
4. Ensures data integrity through regular expression-based input validation.
*/

public class AppDemo extends JFrame {

    // Using a custom Doubly Linked List 'Route' instead of standard Java collections
    private Route deliveryRoute;

    // GUI Components
    private final RoutePanel mapPanel;      // Left side：Interactive map
    private final JTextArea consoleArea;    // Right side: System logs
    private final JLabel nodeInfoLabel;     // Bottom: Selected node information display

    public AppDemo() {

        deliveryRoute = new Route();

        // Basic window interface settings
        setTitle("Delivery Route Optimize System");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top: Title bar
        JLabel titleLabel = new JLabel("Welcome to the Delivery Route Optimize System!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Middle: Split panel (map on the left, log on the right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);

        // Left side Components
        mapPanel = new RoutePanel(deliveryRoute);
        mapPanel.setBorder(new TitledBorder("Live Interactive Map (Click nodes for details)"));
        splitPane.setLeftComponent(mapPanel);

        // Right side Components
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30)); // Background
        consoleArea.setForeground(new Color(100, 255, 100)); // Text
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(new TitledBorder("System Log"));
        splitPane.setRightComponent(scrollPane);

        add(splitPane, BorderLayout.CENTER);

        // Bottom: Composite control area (including information bar + button bar)
        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BorderLayout());

        // Information display area
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 250, 205));
        infoPanel.setBorder(new TitledBorder("Package Details"));
        infoPanel.setPreferredSize(new Dimension(1200, 60));

        nodeInfoLabel = new JLabel("Tip: Click on any blue/red dot on the map to see package details here.");
        nodeInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nodeInfoLabel.setForeground(Color.DARK_GRAY);
        infoPanel.add(nodeInfoLabel);

        southContainer.add(infoPanel, BorderLayout.NORTH);

        // Button control bar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        // Add all buttons
        createButtons(buttonPanel);

        southContainer.add(buttonPanel, BorderLayout.CENTER);

        // Add the bottom container to the window.
        add(southContainer, BorderLayout.SOUTH);

        // Initialization complete.
        log("System initialized. Ready.");
        setVisible(true);
    }

    // Button creation
    private void createButtons(JPanel panel) {
        JButton btnGen = new JButton("[1] Generate");
        btnGen.addActionListener(e -> orderGenerator());
        panel.add(btnGen);

        JButton btnView = new JButton("[2] Summary");
        btnView.addActionListener(e -> viewRouteSummary());
        panel.add(btnView);

        JButton btnOpt = new JButton("[3] Optimize");
        btnOpt.setBackground(new Color(40, 167, 69));
        btnOpt.addActionListener(e -> optimizeRoute());
        panel.add(btnOpt);

        JButton btnExp = new JButton("[4] Export CSV");
        btnExp.addActionListener(e -> exportToCSV());
        panel.add(btnExp);

        JButton btnAdd = new JButton("[5] Add Stop");
        btnAdd.addActionListener(e -> addMoreLocation());
        panel.add(btnAdd);

        JButton btnDel = new JButton("[6] Search/Delete");
        btnDel.addActionListener(e -> searchAndDeleteLocation());
        panel.add(btnDel);

        JButton btnExit = new JButton("[7] Exit");
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.addActionListener(e -> System.exit(0));
        panel.add(btnExit);
    }

    // Main functions
    private void log(String msg) {
        consoleArea.append(msg + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    private boolean routeCheck() {
        if (deliveryRoute.getHead() == null) {
            log("Warning: No delivery route found. Please generate one first!");
            JOptionPane.showMessageDialog(this, "No route generated yet!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // [1] Generate order
    private void orderGenerator() {
        deliveryRoute = new Route();
        mapPanel.setRoute(deliveryRoute); // Update panel reference

        Set<String> usedTrackingNumbers = new HashSet<>();
        Random r = new Random();
        int totalStop = r.nextInt(181) + 70; // Generate 70-250 delivery points to simulate real-world work scenarios.

        log("Generating " + totalStop + " random orders.");

        //Randomly generate an 11-digit tracking number starting with 10010, and check if it has been generated before.
        for (int i = 0; i < totalStop; i++) {
            String trackingNumber = "";
            boolean isRepeat = false;
            while (!isRepeat) {
                String prefix = "10010";
                String initialTrackingNumber = prefix + String.format("%06d", r.nextInt(1000000));
                if (!usedTrackingNumbers.contains(initialTrackingNumber)) {
                    trackingNumber = initialTrackingNumber;
                    usedTrackingNumbers.add(initialTrackingNumber);
                    isRepeat = true;
                }
            }
            // The orders are generated within a 1000 x 1000 matrix, simulating the size of a real world city.
            Location newStop = new Location(trackingNumber, r.nextInt(1000), r.nextInt(1000), i + 1);
            deliveryRoute.addLocation(newStop);
        }

        log("Generation complete.");
        mapPanel.repaint(); // Refresh map
        nodeInfoLabel.setText("Route has been generated. Click [3] to optimize.");
    }

    // [2] The purpose of viewing the total number of generated orders and the total mileage is to compare and optimize the previous routes.
    private void viewRouteSummary() {
        if (!routeCheck()) return;
        log("Route Summary:");
        log("Total Stops: " + deliveryRoute.getSize());
        log(String.format("Current Mileage: %.2f miles", deliveryRoute.totalDistance() / 100));
    }

    /* [3] To optimize the route, each delivery stop in reality should be randomly scattered throughout the city.
    The core idea is that before departing from each stop, picks the node closest to the origin (Warehouse) as the start.
    the system compares which nearby point is the closest, and each departure is to the location closest to the current position.*/
    private void optimizeRoute() {
        if (!routeCheck()) return;

        double oldDistance = deliveryRoute.totalDistance();
        log("Optimizing your route...");

        deliveryRoute.optimizeRoute();

        double newDistance = deliveryRoute.totalDistance();
        log(String.format("Optimization Done! Mileage: %.2f -> %.2f miles", oldDistance/100, newDistance/100));
        log(String.format("Saved: %.2f miles", (oldDistance - newDistance)/100));

        mapPanel.repaint();
        nodeInfoLabel.setText("Route Optimized!");
    }

    /* [4] Exporting a document in CSV format is a common requirement in my work,
    especially when I need to add information about delivered packages to an Excel spreadsheet.*/
    private void exportToCSV() {
        if (!routeCheck()) return;
        String fileName = "delivery_route.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Sequence,Tracking Number,X,Y,SerialNumber\n");
            Node current = deliveryRoute.getHead();
            int index = 1;
            while (current != null) {
                //Import using the specified format.
                Location loc = current.getCurrentLocation();
                writer.write(String.format("%d,%s,%d,%d,%d\n", index, loc.getTrackingNumber(), loc.getX(), loc.getY(), loc.getSerialNumber()));
                current = current.getNext();
                index++;
            }
            log("Export successful to " + fileName);
            JOptionPane.showMessageDialog(this, "Export successful!");
        } catch (IOException e) {
            log("Export failed: " + e.getMessage());
        }
    }

    /* [5] Sometimes situations arise where you need to add packages to an existing route.
    Therefore, a new feature has been added that allows packages to be added directly to the list
    and re-sorted without disrupting the original route.*/
    private void addMoreLocation() {
        if (!routeCheck()) return;

        String input = JOptionPane.showInputDialog(this, "Enter Tracking Number (10010 + 6 digits):");
        if (input == null) return;

        // Input Validation using Regular Expressions. Must start with "10010" followed by exactly 6 digits.
        if (!input.trim().matches("10010\\d{6}")) {
            JOptionPane.showMessageDialog(this, "Invalid Format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Random r = new Random();
        Location newLocation = new Location(input.trim(), r.nextInt(1000), r.nextInt(1000), deliveryRoute.getSize() + 1);

        deliveryRoute.addLocation(newLocation);

        log("Added stop: " + input);

        deliveryRoute.optimizeRoute();
        log("Route auto-reoptimized.");
        mapPanel.repaint();
    }

    /* [6] Package sorting errors occur frequently; packages that don't belong to my delivery area are added to my route.
     I have to remove them and return them to the warehouse, so the deletion function is especially important.*/
    private void searchAndDeleteLocation() {
        if (!routeCheck()) return;

        String input = JOptionPane.showInputDialog(this, "Enter Tracking Number to Delete:");
        if (input == null) return;
        String targetNumber = input.trim();

        Node found = deliveryRoute.findTrackingNumber(targetNumber);

        if (found != null) {
            Location location = found.getCurrentLocation();
            log("Found package: " + targetNumber + " at (" + location.getX() + "," + location.getY() + ")");

            // Highlight the selected point on the map (update the information panel).
            nodeInfoLabel.setText("Package found: " + targetNumber + ". Do you want to delete it?");
            nodeInfoLabel.setForeground(Color.RED);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete Package " + targetNumber + "?\nLocation: " + location.getX() + ", " + location.getY(),
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call the deletion method.
                if (deliveryRoute.deleteLocation(targetNumber)) {
                    deliveryRoute.optimizeRoute(); // Reorder after deletion is complete.
                    log("Deleted " + targetNumber + " and re-optimized route.");
                    mapPanel.repaint();
                    nodeInfoLabel.setText("Package deleted.");
                    nodeInfoLabel.setForeground(Color.BLACK);
                } else {
                    log("Error during deletion.");
                }
            }
        } else {
            log("Package " + targetNumber + "was not found, Please double check your tracking Number!");
            JOptionPane.showMessageDialog(this, "Package was not found, Please double check your tracking Number!");
        }
    }


    class RoutePanel extends JPanel {
        private Route route;

        public RoutePanel(Route route) {
            this.route = route;
            setBackground(new Color(245, 245, 245));

            // Mouse click listener
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick(e.getX(), e.getY());
                }
            });
        }

        public void setRoute(Route route) {
            this.route = route;
        }

        // Handling click events
        private void handleClick(int mouseX, int mouseY) {
            if (route == null || route.getHead() == null) return;

            // The current proportions must be recalculated.
            double scaleX = (double) getWidth() / 1100;
            double scaleY = (double) getHeight() / 1100;

            Node current = route.getHead();
            boolean found = false;

            while (current != null) {
                Location loc = current.getCurrentLocation();
                // Convert coordinates
                int drawX = (int) (loc.getX() * scaleX) + 40;
                int drawY = (int) (loc.getY() * scaleY) + 40;

                // Calculate the click distance.
                if (Math.abs(mouseX - drawX) < 10 && Math.abs(mouseY - drawY) < 10) {
                    // Update the bottom information bar.
                    String info = String.format("PACKAGE INFO | Tracking: %s | SN: %d | Loc: (%d, %d)",
                            loc.getTrackingNumber(), loc.getSerialNumber(), loc.getX(), loc.getY());
                    nodeInfoLabel.setText(info);
                    nodeInfoLabel.setForeground(new Color(0, 102, 204));
                    log("Clicked map node: " + loc.getTrackingNumber());
                    found = true;
                    break;
                }
                current = current.getNext();
            }

            if (!found) {
                nodeInfoLabel.setText("Click on a dot to view details...");
                nodeInfoLabel.setForeground(Color.DARK_GRAY);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Enable Anti-aliasing for smoother rendering (High-quality graphics)
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (route == null || route.getHead() == null) {
                g2.setColor(Color.GRAY);
                g2.drawString("No Route Data. Please Generate Orders.", getWidth()/2 - 100, getHeight()/2);
                return;
            }

            // Dynamic scaling
            double scaleX = (double) getWidth() / 1100;
            double scaleY = (double) getHeight() / 1100;

            Node current = route.getHead();

            //
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(2));
            while (current != null && current.getNext() != null) {
                Location p1 = current.getCurrentLocation();
                Location p2 = current.getNext().getCurrentLocation();

                int x1 = (int) (p1.getX() * scaleX) + 40;
                int y1 = (int) (p1.getY() * scaleY) + 40;
                int x2 = (int) (p2.getX() * scaleX) + 40;
                int y2 = (int) (p2.getY() * scaleY) + 40;

                g2.drawLine(x1, y1, x2, y2);
                current = current.getNext();
            }

            //
            current = route.getHead();
            while (current != null) {
                Location loc = current.getCurrentLocation();
                int x = (int) (loc.getX() * scaleX) + 40;
                int y = (int) (loc.getY() * scaleY) + 40;

                if (current == route.getHead()) {
                    g2.setColor(new Color(220, 53, 69)); // 起点红
                    g2.fillOval(x - 6, y - 6, 12, 12);
                    g2.drawString("START", x - 10, y - 10);
                } else {
                    g2.setColor(new Color(0, 123, 255)); // 其他蓝
                    g2.fillOval(x - 4, y - 4, 8, 8);
                }
                current = current.getNext();
            }
        }
    }

    // Program entry
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppDemo());
    }
}
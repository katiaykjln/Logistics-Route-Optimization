package OptimizeRoute;

import javax.swing.*;
import java.awt.*;

public class RoutePanel extends JPanel {

    private Route route;
    private double scaleFactor = 0.6;

    public RoutePanel(Route route) {
        this.route = route;
        this.setBackground(new Color(245, 245, 245));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(route.getHead() == null) return;

        Node currentNode = route.getHead();

        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(2));

        while (currentNode != null && currentNode.getNext() != null) {
            Location p1 = currentNode.getCurrentLocation();
            Location p2 = currentNode.getNext().getCurrentLocation();

            g2.drawLine((int)(p1.getX() * scaleFactor), (int)(p1.getY() * scaleFactor),
                    (int)(p2.getX() * scaleFactor), (int)(p2.getY() * scaleFactor));

            currentNode = currentNode.getNext();
        }
        currentNode = route.getHead();
        while (currentNode != null) {
            Location loc = currentNode.getCurrentLocation();
            int x = (int)(loc.getX() * scaleFactor);
            int y = (int)(loc.getY() * scaleFactor);

            if (currentNode == route.getHead()) {
                g2.setColor(new Color(220, 53, 69)); // 红色
                g2.fillOval(x - 8, y - 8, 16, 16); // 画圆
                g2.setColor(Color.WHITE);
                g2.drawString("Start", x - 10, y - 10);
            } else {
                g2.setColor(new Color(0, 123, 255)); // 蓝色
                g2.fillOval(x - 5, y - 5, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(loc.getSerialNumber()), x + 8, y + 8);
            }

            currentNode = currentNode.getNext();

         }
    }
}

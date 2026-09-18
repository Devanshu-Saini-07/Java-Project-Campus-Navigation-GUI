import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Graphical Campus Map implemented using Java AWT Canvas and 2D Graphics.
 * Renders buildings, pathways, bus stops, highlights selected locations, and displays calculated routes.
 */
public class CampusMap extends Canvas implements MouseListener {

    public interface MapSelectionListener {
        void onLocationSelected(CampusLocation location);
    }

    private CampusDataManager dataManager;
    private CampusLocation selectedLocation;
    private List<CampusLocation> activeRoute;
    private MapSelectionListener selectionListener;
    private boolean isDarkMode = false;

    // Node rendering dimensions
    private static final int NODE_WIDTH = 80;
    private static final int NODE_HEIGHT = 40;

    public CampusMap(CampusDataManager dataManager) {
        this.dataManager = dataManager;
        this.activeRoute = null;
        this.selectedLocation = null;
        addMouseListener(this);
    }

    public void setSelectionListener(MapSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void setSelectedLocation(CampusLocation location) {
        this.selectedLocation = location;
        repaint();
    }

    public void setActiveRoute(List<CampusLocation> route) {
        this.activeRoute = route;
        repaint();
    }

    public void clearHighlights() {
        this.selectedLocation = null;
        this.activeRoute = null;
        repaint();
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(620, 390);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(500, 320);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int width = getWidth();
        int height = getHeight();

        // Background
        if (isDarkMode) {
            g.setColor(new Color(30, 33, 38));
            g.fillRect(0, 0, width, height);

            // Subtle grid
            g.setColor(new Color(45, 49, 56));
            for (int x = 0; x < width; x += 40) g.drawLine(x, 0, x, height);
            for (int y = 0; y < height; y += 40) g.drawLine(0, y, width, y);
        } else {
            g.setColor(new Color(240, 245, 250));
            g.fillRect(0, 0, width, height);

            // Campus grassy background patches
            g.setColor(new Color(230, 242, 230));
            g.fillRoundRect(20, 20, width - 40, height - 40, 25, 25);

            // Subtle grid
            g.setColor(new Color(215, 230, 220));
            for (int x = 30; x < width - 30; x += 40) g.drawLine(x, 20, x, height - 20);
            for (int y = 30; y < height - 30; y += 40) g.drawLine(20, y, width - 20, y);
        }

        // Title banner on canvas
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(isDarkMode ? new Color(180, 190, 200) : new Color(60, 80, 100));
        g.drawString("Interactive Campus Map (Click any building to view details)", 25, 32);

        // Draw Pathways / Edges
        CampusGraph graph = dataManager.getGraph();
        if (graph != null) {
            List<CampusEdge> edges = graph.getAllEdges();
            for (CampusEdge edge : edges) {
                CampusLocation src = graph.getLocation(edge.getSourceId());
                CampusLocation dest = graph.getLocation(edge.getDestinationId());
                if (src != null && dest != null) {
                    drawPath(g, src.getX(), src.getY(), dest.getX(), dest.getY(), edge.getDistance(), false);
                }
            }
        }

        // Highlight Active Shortest Path Route
        if (activeRoute != null && activeRoute.size() >= 2) {
            for (int i = 0; i < activeRoute.size() - 1; i++) {
                CampusLocation from = activeRoute.get(i);
                CampusLocation to = activeRoute.get(i + 1);
                drawPath(g, from.getX(), from.getY(), to.getX(), to.getY(), 0, true);
            }
        }

        // Draw Locations / Buildings
        Collection<CampusLocation> locs = dataManager.getAllLocations();
        for (CampusLocation loc : locs) {
            drawLocationNode(g, loc);
        }

        // Draw Legend in Bottom Left
        drawLegend(g, width, height);
    }

    private void drawPath(Graphics g, int x1, int y1, int x2, int y2, int distance, boolean isHighlighted) {
        if (isHighlighted) {
            // Draw thick route line
            g.setColor(new Color(255, 87, 34)); // Vibrant Orange
            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x1 + 1, y1, x2 + 1, y2);
            g.drawLine(x1 - 1, y1, x2 - 1, y2);
            g.drawLine(x1, y1 + 1, x2, y2 + 1);
            g.drawLine(x1, y1 - 1, x2, y2 - 1);
        } else {
            g.setColor(isDarkMode ? new Color(90, 100, 115) : new Color(160, 175, 190));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawLocationNode(Graphics g, CampusLocation loc) {
        int x = loc.getX() - (NODE_WIDTH / 2);
        int y = loc.getY() - (NODE_HEIGHT / 2);
        boolean isSelected = (selectedLocation != null && selectedLocation.getId().equals(loc.getId()));

        // Pick color based on category
        Color nodeColor;
        Color borderColor = isDarkMode ? new Color(120, 130, 145) : new Color(70, 90, 120);

        String cat = loc.getCategory().toLowerCase();
        if (cat.contains("building") || cat.contains("admin")) {
            nodeColor = isDarkMode ? new Color(30, 80, 140) : new Color(70, 130, 200);
        } else if (cat.contains("hostel")) {
            nodeColor = isDarkMode ? new Color(100, 50, 130) : new Color(150, 90, 180);
        } else if (cat.contains("lab")) {
            nodeColor = isDarkMode ? new Color(140, 80, 20) : new Color(220, 130, 40);
        } else if (cat.contains("department")) {
            nodeColor = isDarkMode ? new Color(20, 110, 90) : new Color(40, 160, 130);
        } else if (cat.contains("bus")) {
            nodeColor = isDarkMode ? new Color(140, 120, 20) : new Color(220, 180, 40);
        } else if (cat.contains("facility") || cat.contains("sports")) {
            nodeColor = isDarkMode ? new Color(40, 110, 50) : new Color(60, 160, 80);
        } else {
            nodeColor = isDarkMode ? new Color(70, 75, 85) : new Color(120, 130, 140);
        }

        // Draw shadow/highlight if selected
        if (isSelected) {
            g.setColor(new Color(255, 215, 0)); // Gold halo
            g.fillRoundRect(x - 5, y - 5, NODE_WIDTH + 10, NODE_HEIGHT + 10, 12, 12);
            g.setColor(new Color(220, 53, 69)); // Red border
            g.drawRoundRect(x - 6, y - 6, NODE_WIDTH + 12, NODE_HEIGHT + 12, 14, 14);
        }

        // Draw Node Background
        g.setColor(nodeColor);
        g.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 8, 8);

        // Draw Node Border
        g.setColor(borderColor);
        g.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 8, 8);

        // Draw Node Label text
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);

        String displayName = loc.getName();
        // Truncate if too long for node box
        if (displayName.length() > 13) {
            displayName = displayName.substring(0, 11) + "..";
        }

        FontMetrics fm = g.getFontMetrics();
        int strWidth = fm.stringWidth(displayName);
        int strX = x + (NODE_WIDTH - strWidth) / 2;
        int strY = y + (NODE_HEIGHT / 2) + 4;

        g.drawString(displayName, strX, strY);

        // Category Tag below box
        g.setFont(new Font("Arial", Font.PLAIN, 8));
        g.setColor(isDarkMode ? new Color(170, 180, 190) : new Color(50, 60, 70));
        String catLabel = loc.getCategory();
        int catWidth = g.getFontMetrics().stringWidth(catLabel);
        g.drawString(catLabel, x + (NODE_WIDTH - catWidth) / 2, y + NODE_HEIGHT + 10);
    }

    private void drawLegend(Graphics g, int width, int height) {
        int lx = 20;
        int ly = height - 25;

        g.setFont(new Font("Arial", Font.PLAIN, 9));
        g.setColor(isDarkMode ? new Color(200, 210, 220) : new Color(40, 50, 60));

        // Legend items
        drawLegendItem(g, lx, ly, isDarkMode ? new Color(30, 80, 140) : new Color(70, 130, 200), "Building");
        drawLegendItem(g, lx + 75, ly, isDarkMode ? new Color(100, 50, 130) : new Color(150, 90, 180), "Hostel");
        drawLegendItem(g, lx + 145, ly, isDarkMode ? new Color(140, 80, 20) : new Color(220, 130, 40), "Lab");
        drawLegendItem(g, lx + 205, ly, isDarkMode ? new Color(20, 110, 90) : new Color(40, 160, 130), "Dept");
        drawLegendItem(g, lx + 265, ly, isDarkMode ? new Color(140, 120, 20) : new Color(220, 180, 40), "Bus Stop");
        drawLegendItem(g, lx + 345, ly, new Color(255, 87, 34), "Route");
    }

    private void drawLegendItem(Graphics g, int x, int y, Color c, String text) {
        g.setColor(c);
        g.fillRect(x, y - 8, 8, 8);
        g.setColor(isDarkMode ? Color.WHITE : Color.BLACK);
        g.drawRect(x, y - 8, 8, 8);
        g.drawString(text, x + 12, y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // Check if any location was clicked
        for (CampusLocation loc : dataManager.getAllLocations()) {
            int x = loc.getX() - (NODE_WIDTH / 2);
            int y = loc.getY() - (NODE_HEIGHT / 2);

            if (mx >= x && mx <= x + NODE_WIDTH && my >= y && my <= y + NODE_HEIGHT) {
                this.selectedLocation = loc;
                repaint();
                if (selectionListener != null) {
                    selectionListener.onLocationSelected(loc);
                }
                return;
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

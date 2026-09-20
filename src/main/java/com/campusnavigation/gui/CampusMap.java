package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.model.CampusEdge;
import com.campusnavigation.repository.CampusDataManager;
import com.campusnavigation.service.CampusGraph;

/**
 * Enhanced Campus Map with coordinate scaling, double buffering, smooth hover effects,
 * and clean geometric category markers.
 */
public class CampusMap extends Canvas implements MouseListener, MouseMotionListener {

    public interface MapSelectionListener {
        void onLocationSelected(CampusLocation location);
    }

    private CampusDataManager dataManager;
    private CampusLocation selectedLocation;
    private CampusLocation hoveredLocation;
    private List<CampusLocation> activeRoute;
    private MapSelectionListener selectionListener;
    private boolean isDarkMode = false;

    // Double buffering offscreen buffer
    private Image offscreenImage;

    // Node rendering dimensions
    private static final int NODE_WIDTH = 70;
    private static final int NODE_HEIGHT = 30;
    private static final int HOVER_PADDING = 5;
    private static final int MAP_PADDING = 50;

    public CampusMap(CampusDataManager dataManager) {
        this.dataManager = dataManager;
        this.activeRoute = null;
        this.selectedLocation = null;
        this.hoveredLocation = null;
        addMouseListener(this);
        addMouseMotionListener(this);
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
        this.hoveredLocation = null;
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

    /**
     * Translates a location's world coordinates to scaled screen coordinates
     * preserving aspect ratio and adding padding.
     */
    public Point getScreenPos(CampusLocation loc) {
        if (loc == null) return new Point(0, 0);
        return getScreenPos(loc.getX(), loc.getY());
    }

    /**
     * Translates raw X and Y coordinates to scaled screen coordinates.
     */
    public Point getScreenPos(int rawX, int rawY) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            width = getPreferredSize().width;
            height = getPreferredSize().height;
        }

        Collection<CampusLocation> locs = (dataManager != null) ? dataManager.getAllLocations() : Collections.emptyList();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        if (locs != null && !locs.isEmpty()) {
            for (CampusLocation loc : locs) {
                minX = Math.min(minX, loc.getX());
                maxX = Math.max(maxX, loc.getX());
                minY = Math.min(minY, loc.getY());
                maxY = Math.max(maxY, loc.getY());
            }
        }

        if (minX == Integer.MAX_VALUE || minX == maxX || minY == maxY) {
            minX = 50; maxX = 550;
            minY = 50; maxY = 350;
        }

        int spanX = Math.max(1, maxX - minX);
        int spanY = Math.max(1, maxY - minY);

        int padX = MAP_PADDING + (NODE_WIDTH / 2);
        int padY = MAP_PADDING + (NODE_HEIGHT / 2);

        int availW = Math.max(20, width - 2 * padX);
        int availH = Math.max(20, height - 2 * padY);

        double scale = Math.min((double) availW / spanX, (double) availH / spanY);
        double mapDrawW = spanX * scale;
        double mapDrawH = spanY * scale;

        double offsetX = (width - mapDrawW) / 2.0 - minX * scale;
        double offsetY = (height - mapDrawH) / 2.0 - minY * scale;

        int sx = (int) Math.round(rawX * scale + offsetX);
        int sy = (int) Math.round(rawY * scale + offsetY);
        return new Point(sx, sy);
    }

    /**
     * Override update() to eliminate flickering by delegating directly to paint().
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Double-buffered rendering for smooth visual updates.
     */
    @Override
    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        if (offscreenImage == null || offscreenImage.getWidth(this) != width || offscreenImage.getHeight(this) != height) {
            offscreenImage = createImage(width, height);
        }

        if (offscreenImage == null) return;

        Graphics2D g2d = (Graphics2D) offscreenImage.getGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            renderMap(g2d, width, height);
        } finally {
            g2d.dispose();
        }

        g.drawImage(offscreenImage, 0, 0, this);
    }

    private void renderMap(Graphics2D g2d, int width, int height) {
        // Background
        if (isDarkMode) {
            g2d.setColor(new Color(24, 28, 36));
            g2d.fillRect(0, 0, width, height);
            // Subtle grid
            g2d.setColor(new Color(36, 42, 54));
            for (int x = 0; x < width; x += 35) g2d.drawLine(x, 0, x, height);
            for (int y = 0; y < height; y += 35) g2d.drawLine(0, y, width, y);
        } else {
            g2d.setColor(new Color(240, 245, 250));
            g2d.fillRect(0, 0, width, height);
            // Grassy background
            g2d.setColor(new Color(230, 242, 232));
            g2d.fillRoundRect(15, 15, width - 30, height - 30, 20, 20);
            // Subtle grid
            g2d.setColor(new Color(218, 232, 222));
            for (int x = 25; x < width - 25; x += 35) g2d.drawLine(x, 15, x, height - 15);
            for (int y = 25; y < height - 25; y += 35) g2d.drawLine(15, y, width - 15, y);
        }

        // Title banner
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(isDarkMode ? new Color(180, 195, 215) : new Color(50, 75, 105));
        g2d.drawString("Interactive Campus Map — Click any building to view details", 25, 30);

        // Draw Pathways
        drawPathways(g2d);

        // Highlight Active Route
        if (activeRoute != null && activeRoute.size() >= 2) {
            for (int i = 0; i < activeRoute.size() - 1; i++) {
                CampusLocation from = activeRoute.get(i);
                CampusLocation to = activeRoute.get(i + 1);
                Point p1 = getScreenPos(from);
                Point p2 = getScreenPos(to);
                drawPath(g2d, p1.x, p1.y, p2.x, p2.y, 0, true);
            }
        }

        // Draw Location Nodes
        if (dataManager != null) {
            Collection<CampusLocation> locs = dataManager.getAllLocations();
            for (CampusLocation loc : locs) {
                drawLocationNode(g2d, loc);
            }
        }

        // Draw Legend
        drawLegend(g2d, width, height);
    }

    private void drawPathways(Graphics2D g2d) {
        if (dataManager == null || dataManager.getGraph() == null) return;
        CampusGraph graph = dataManager.getGraph();
        List<CampusEdge> edges = graph.getAllEdges();
        if (edges == null) return;

        for (CampusEdge edge : edges) {
            CampusLocation src = dataManager.getLocationById(edge.getSourceId());
            CampusLocation dest = dataManager.getLocationById(edge.getDestinationId());
            if (src != null && dest != null) {
                Point p1 = getScreenPos(src);
                Point p2 = getScreenPos(dest);
                drawPath(g2d, p1.x, p1.y, p2.x, p2.y, edge.getDistance(), false);
            }
        }
    }

    private void drawPath(Graphics2D g2d, int x1, int y1, int x2, int y2, int distance, boolean isHighlighted) {
        if (isHighlighted) {
            // Thick active route line
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(255, 87, 34));
            g2d.drawLine(x1, y1, x2, y2);
            drawArrow(g2d, x1, y1, x2, y2);
        } else {
            // Dashed pathway
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[]{4.0f, 4.0f}, 0));
            g2d.setColor(isDarkMode ? new Color(80, 95, 115) : new Color(150, 170, 185));
            g2d.drawLine(x1, y1, x2, y2);

            if (distance > 0) {
                int midX = (x1 + x2) / 2;
                int midY = (y1 + y2) / 2;
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.setColor(isDarkMode ? new Color(140, 155, 170) : new Color(90, 110, 130));
                g2d.drawString(distance + "m", midX + 3, midY - 3);
            }
        }
        g2d.setStroke(new BasicStroke(1));
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        int arrowSize = 7;

        int[] xPoints = {
            midX + (int) Math.round(arrowSize * Math.cos(angle)),
            midX - (int) Math.round(arrowSize * Math.cos(angle - Math.PI / 6)),
            midX - (int) Math.round(arrowSize * Math.cos(angle + Math.PI / 6))
        };
        int[] yPoints = {
            midY + (int) Math.round(arrowSize * Math.sin(angle)),
            midY - (int) Math.round(arrowSize * Math.sin(angle - Math.PI / 6)),
            midY - (int) Math.round(arrowSize * Math.sin(angle + Math.PI / 6))
        };

        Color prevColor = g2d.getColor();
        g2d.setColor(new Color(230, 50, 20));
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(prevColor);
    }

    private void drawLocationNode(Graphics2D g2d, CampusLocation loc) {
        Point pos = getScreenPos(loc);
        int x = pos.x - (NODE_WIDTH / 2);
        int y = pos.y - (NODE_HEIGHT / 2);

        boolean isSelected = (selectedLocation != null && selectedLocation.getId().equals(loc.getId()));
        boolean isHovered = (hoveredLocation != null && hoveredLocation.getId().equals(loc.getId()));

        Color nodeColor = getCategoryColor(loc.getCategory());
        Color borderColor = isDarkMode ? new Color(130, 145, 165) : new Color(50, 70, 95);

        // Hover halo
        if (isHovered) {
            g2d.setColor(new Color(255, 215, 0, 120));
            g2d.fillRoundRect(x - HOVER_PADDING, y - HOVER_PADDING,
                    NODE_WIDTH + 2 * HOVER_PADDING, NODE_HEIGHT + 2 * HOVER_PADDING, 10, 10);
        }

        // Selection highlight
        if (isSelected) {
            g2d.setColor(new Color(255, 59, 48));
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawRoundRect(x - 3, y - 3, NODE_WIDTH + 6, NODE_HEIGHT + 6, 9, 9);
        }

        // Node background
        g2d.setColor(nodeColor);
        g2d.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 8, 8);

        // Node border
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.setColor(borderColor);
        g2d.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 8, 8);

        // Draw geometric category icon
        drawCategoryIcon(g2d, loc.getCategory(), x + (NODE_WIDTH - 8) / 2, y + 4);

        // Draw node label
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.setColor(Color.WHITE);

        String displayName = loc.getName();
        FontMetrics fm = g2d.getFontMetrics();
        int maxTextW = NODE_WIDTH - 6;
        if (fm.stringWidth(displayName) > maxTextW) {
            while (displayName.length() > 3 && fm.stringWidth(displayName + "..") > maxTextW) {
                displayName = displayName.substring(0, displayName.length() - 1);
            }
            displayName = displayName + "..";
        }

        int strX = x + (NODE_WIDTH - fm.stringWidth(displayName)) / 2;
        int strY = y + 23;

        g2d.drawString(displayName, strX, strY);
    }

    /**
     * Renders a crisp geometric icon corresponding to the location category.
     * Avoids font-dependent emoji rendering glitches.
     */
    private void drawCategoryIcon(Graphics2D g2d, String category, int x, int y) {
        String cat = (category != null) ? category.toLowerCase() : "";
        g2d.setColor(Color.WHITE);

        if (cat.contains("building") || cat.contains("admin")) {
            // Building icon: small block with windows
            g2d.fillRect(x, y + 1, 8, 6);
            g2d.setColor(new Color(180, 210, 245));
            g2d.fillRect(x + 1, y + 2, 2, 2);
            g2d.fillRect(x + 5, y + 2, 2, 2);
        } else if (cat.contains("hostel")) {
            // Hostel icon: house with pitched roof
            int[] px = {x + 4, x, x + 8};
            int[] py = {y, y + 4, y + 4};
            g2d.fillPolygon(px, py, 3);
            g2d.fillRect(x + 1, y + 4, 6, 3);
        } else if (cat.contains("lab")) {
            // Lab icon: flask
            g2d.fillRect(x + 3, y, 2, 2);
            int[] px = {x + 3, x + 5, x + 8, x};
            int[] py = {y + 2, y + 2, y + 7, y + 7};
            g2d.fillPolygon(px, py, 4);
        } else if (cat.contains("department")) {
            // Department icon: book
            g2d.fillRect(x, y + 1, 3, 6);
            g2d.fillRect(x + 5, y + 1, 3, 6);
            g2d.drawLine(x + 3, y + 1, x + 4, y + 1);
            g2d.drawLine(x + 3, y + 7, x + 4, y + 7);
        } else if (cat.contains("bus")) {
            // Bus stop icon: bus outline with wheels
            g2d.fillRoundRect(x, y + 1, 8, 5, 2, 2);
            g2d.setColor(new Color(70, 70, 70));
            g2d.fillOval(x + 1, y + 5, 2, 2);
            g2d.fillOval(x + 5, y + 5, 2, 2);
        } else if (cat.contains("facility") || cat.contains("sports")) {
            // Sports / Facility icon: concentric ring
            g2d.fillOval(x, y, 8, 8);
            g2d.setColor(new Color(60, 160, 80));
            g2d.fillOval(x + 2, y + 2, 4, 4);
        } else if (cat.contains("entry") || cat.contains("gate")) {
            // Entry / Gate icon: portal arch
            int[] px = {x + 4, x, x + 8};
            int[] py = {y, y + 4, y + 4};
            g2d.fillPolygon(px, py, 3);
            g2d.fillRect(x + 3, y + 3, 2, 4);
        } else {
            // Default location pin
            g2d.fillOval(x + 1, y, 6, 6);
            g2d.drawLine(x + 4, y + 6, x + 4, y + 7);
        }
    }

    private Color getCategoryColor(String category) {
        String cat = (category != null) ? category.toLowerCase() : "";
        if (cat.contains("building") || cat.contains("admin")) {
            return isDarkMode ? new Color(30, 80, 140) : new Color(50, 115, 190);
        } else if (cat.contains("hostel")) {
            return isDarkMode ? new Color(100, 50, 130) : new Color(140, 80, 170);
        } else if (cat.contains("lab")) {
            return isDarkMode ? new Color(140, 80, 20) : new Color(200, 110, 30);
        } else if (cat.contains("department")) {
            return isDarkMode ? new Color(20, 110, 90) : new Color(30, 145, 115);
        } else if (cat.contains("bus")) {
            return isDarkMode ? new Color(140, 115, 20) : new Color(195, 150, 25);
        } else if (cat.contains("facility") || cat.contains("sports")) {
            return isDarkMode ? new Color(40, 110, 50) : new Color(50, 145, 70);
        } else if (cat.contains("entry") || cat.contains("gate")) {
            return isDarkMode ? new Color(110, 50, 60) : new Color(170, 70, 80);
        } else {
            return isDarkMode ? new Color(70, 75, 85) : new Color(110, 120, 130);
        }
    }

    private void drawLegend(Graphics2D g2d, int width, int height) {
        int lx = 20;
        int ly = height - 12;

        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        g2d.setColor(isDarkMode ? new Color(200, 210, 220) : new Color(40, 55, 70));
        g2d.drawString("Legend:", lx, ly);

        int curX = lx + 45;
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Building"), "Building");
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Hostel"), "Hostel");
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Lab"), "Lab");
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Dept"), "Dept");
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Bus Stop"), "Bus");
        curX = drawLegendItem(g2d, curX, ly, getCategoryColor("Facility"), "Facility");
        drawLegendRouteItem(g2d, curX, ly, new Color(255, 87, 34), "Route");
    }

    private int drawLegendItem(Graphics2D g2d, int x, int y, Color c, String text) {
        g2d.setColor(c);
        g2d.fillRoundRect(x, y - 8, 8, 8, 2, 2);
        g2d.setColor(isDarkMode ? new Color(160, 175, 190) : new Color(70, 85, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y - 8, 8, 8, 2, 2);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(isDarkMode ? new Color(200, 210, 220) : new Color(50, 65, 80));
        g2d.drawString(text, x + 11, y);

        FontMetrics fm = g2d.getFontMetrics();
        return x + 11 + fm.stringWidth(text) + 12;
    }

    private void drawLegendRouteItem(Graphics2D g2d, int x, int y, Color c, String text) {
        g2d.setColor(c);
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x, y - 4, x + 10, y - 4);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(isDarkMode ? new Color(200, 210, 220) : new Color(50, 65, 80));
        g2d.drawString(text, x + 13, y);
        g2d.setStroke(new BasicStroke(1));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (dataManager == null) return;

        for (CampusLocation loc : dataManager.getAllLocations()) {
            Point pos = getScreenPos(loc);
            int x = pos.x - (NODE_WIDTH / 2);
            int y = pos.y - (NODE_HEIGHT / 2);

            if (mx >= x - HOVER_PADDING && mx <= x + NODE_WIDTH + HOVER_PADDING &&
                my >= y - HOVER_PADDING && my <= y + NODE_HEIGHT + HOVER_PADDING) {
                this.selectedLocation = loc;
                repaint();
                if (selectionListener != null) {
                    selectionListener.onLocationSelected(loc);
                }
                return;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        CampusLocation prevHovered = hoveredLocation;

        hoveredLocation = null;
        if (dataManager != null) {
            for (CampusLocation loc : dataManager.getAllLocations()) {
                Point pos = getScreenPos(loc);
                int x = pos.x - (NODE_WIDTH / 2);
                int y = pos.y - (NODE_HEIGHT / 2);

                if (mx >= x - HOVER_PADDING && mx <= x + NODE_WIDTH + HOVER_PADDING &&
                    my >= y - HOVER_PADDING && my <= y + NODE_HEIGHT + HOVER_PADDING) {
                    hoveredLocation = loc;
                    break;
                }
            }
        }

        boolean changed = false;
        if (prevHovered == null && hoveredLocation != null) {
            changed = true;
        } else if (prevHovered != null && hoveredLocation == null) {
            changed = true;
        } else if (prevHovered != null && hoveredLocation != null && !prevHovered.getId().equals(hoveredLocation.getId())) {
            changed = true;
        }

        if (hoveredLocation != null) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (changed) {
            repaint();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) { setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
    @Override public void mouseExited(MouseEvent e) {
        if (hoveredLocation != null) {
            hoveredLocation = null;
            repaint();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    @Override public void mouseDragged(MouseEvent e) {}
}
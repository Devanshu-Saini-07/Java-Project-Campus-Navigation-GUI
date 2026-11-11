package com.campusnavigation.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import com.campusnavigation.model.RoomInfo;
import com.campusnavigation.model.CampusLocation;
import com.campusnavigation.repository.CampusDataManager;

/**
 * Interactive Multi-Floor Indoor Architectural Floor Plan Viewer.
 * Displays interactive 2D indoor layouts of academic blocks and admin buildings,
 * color-coded by room category (Classrooms, Labs, Faculty, Elevators/Ramps, Exits).
 */
public class IndoorFloorPlan extends Panel implements ActionListener, ItemListener {

    public interface FloorPlanCallback {
        void onRouteToBuilding(String buildingName);
        void onCloseFloorPlan();
    }

    private CampusDataManager dataManager;
    private FloorPlanCallback callback;
    private boolean isDarkMode = false;

    // Top Controls
    private Panel topControls;
    private Choice buildingChoice;
    private Choice floorChoice;
    private Button btnBack;
    private Button btnRouteToBuilding;
    private Label lblTitle;

    // Center Canvas
    private FloorCanvas floorCanvas;

    // Bottom Room Info Bar
    private Panel bottomBar;
    private Label roomDetailsLabel;
    private Label roomDescLabel;

    public IndoorFloorPlan(CampusDataManager dataManager, FloorPlanCallback callback) {
        this.dataManager = dataManager;
        this.callback = callback;

        setLayout(new BorderLayout(6, 6));
        setBackground(new Color(245, 247, 250));
        initializeComponents();
        buildLayout();
    }

    private void initializeComponents() {
        topControls = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        topControls.setBackground(new Color(235, 242, 252));

        lblTitle = new Label("Indoor Multi-Floor Layout:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setForeground(new Color(25, 45, 90));
        lblTitle.setBackground(new Color(235, 242, 252));

        buildingChoice = new Choice();
        buildingChoice.add("Academic Block A");
        buildingChoice.add("Academic Block B");
        buildingChoice.add("Admin Block");
        buildingChoice.add("Central Library");
        buildingChoice.addItemListener(this);

        floorChoice = new Choice();
        floorChoice.add("Ground Floor");
        floorChoice.add("1st Floor");
        floorChoice.add("2nd Floor");
        floorChoice.add("3rd Floor");
        floorChoice.select(2); // default 2nd floor (CSE & AI labs)
        floorChoice.addItemListener(this);

        btnRouteToBuilding = new Button("Route to Building on Map");
        btnRouteToBuilding.setBackground(new Color(35, 120, 215));
        btnRouteToBuilding.setForeground(Color.WHITE);
        btnRouteToBuilding.setFont(new Font("Arial", Font.BOLD, 11));
        btnRouteToBuilding.addActionListener(this);

        btnBack = new Button("< Back to Campus Map");
        btnBack.setBackground(new Color(75, 85, 105));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 11));
        btnBack.addActionListener(this);

        topControls.add(lblTitle);
        topControls.add(buildingChoice);
        topControls.add(floorChoice);
        topControls.add(btnRouteToBuilding);
        topControls.add(btnBack);

        // Center Floor Canvas
        floorCanvas = new FloorCanvas();

        // Bottom Bar
        bottomBar = new Panel(new GridLayout(2, 1, 0, 2));
        bottomBar.setBackground(new Color(230, 238, 248));

        roomDetailsLabel = new Label("Hover or click any room above to inspect department, faculty, and room amenities.");
        roomDetailsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roomDetailsLabel.setForeground(new Color(25, 45, 80));
        roomDetailsLabel.setBackground(new Color(230, 238, 248));

        roomDescLabel = new Label("[Accessible] Elevator and wheelchair ramps available at Central Core corridor.");
        roomDescLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        roomDescLabel.setForeground(new Color(70, 85, 110));
        roomDescLabel.setBackground(new Color(230, 238, 248));

        bottomBar.add(roomDetailsLabel);
        bottomBar.add(roomDescLabel);
    }

    private void buildLayout() {
        add(topControls, BorderLayout.NORTH);
        add(floorCanvas, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void selectBuildingAndFloor(String building, String floor) {
        if (building != null) {
            for (int i = 0; i < buildingChoice.getItemCount(); i++) {
                if (buildingChoice.getItem(i).equalsIgnoreCase(building) ||
                    building.contains(buildingChoice.getItem(i))) {
                    buildingChoice.select(i);
                    break;
                }
            }
        }
        if (floor != null) {
            for (int i = 0; i < floorChoice.getItemCount(); i++) {
                if (floorChoice.getItem(i).toLowerCase().contains(floor.toLowerCase())) {
                    floorChoice.select(i);
                    break;
                }
            }
        }
        floorCanvas.repaint();
    }

    public void applyTheme(boolean dark) {
        this.isDarkMode = dark;
        Color bg = dark ? new Color(20, 25, 35) : new Color(245, 247, 250);
        Color topBg = dark ? new Color(26, 32, 45) : new Color(235, 242, 252);
        Color textFg = dark ? new Color(210, 225, 250) : new Color(25, 45, 90);

        setBackground(bg);
        if (topControls != null) topControls.setBackground(topBg);
        if (lblTitle != null) {
            lblTitle.setBackground(topBg);
            lblTitle.setForeground(textFg);
        }
        if (bottomBar != null) bottomBar.setBackground(dark ? new Color(26, 32, 45) : new Color(230, 238, 248));
        Color btmBg = dark ? new Color(26, 32, 45) : new Color(230, 238, 248);
        if (roomDetailsLabel != null) {
            roomDetailsLabel.setBackground(btmBg);
            roomDetailsLabel.setForeground(dark ? new Color(210, 230, 255) : new Color(25, 45, 80));
        }
        if (roomDescLabel != null) {
            roomDescLabel.setBackground(btmBg);
            roomDescLabel.setForeground(dark ? new Color(160, 175, 200) : new Color(70, 85, 110));
        }

        if (buildingChoice != null) {
            buildingChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            buildingChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }
        if (floorChoice != null) {
            floorChoice.setBackground(dark ? new Color(38, 44, 58) : Color.WHITE);
            floorChoice.setForeground(dark ? Color.WHITE : Color.BLACK);
        }

        floorCanvas.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRouteToBuilding) {
            if (callback != null) {
                callback.onRouteToBuilding(buildingChoice.getSelectedItem());
            }
        } else if (e.getSource() == btnBack) {
            if (callback != null) {
                callback.onCloseFloorPlan();
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        floorCanvas.repaint();
    }

    /**
     * 2D Canvas rendering the architectural indoor floor plan.
     */
    private class FloorCanvas extends Canvas implements MouseListener, MouseMotionListener {
        private Image offscreen;
        private RoomBox hoveredRoom = null;
        private RoomBox selectedRoom = null;

        public FloorCanvas() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            if (offscreen == null || offscreen.getWidth(this) != w || offscreen.getHeight(this) != h) {
                offscreen = createImage(w, h);
            }
            if (offscreen == null) return;

            Graphics2D g2 = (Graphics2D) offscreen.getGraphics();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                renderFloor(g2, w, h);
            } finally {
                g2.dispose();
            }

            g.drawImage(offscreen, 0, 0, this);
        }

        private void renderFloor(Graphics2D g2, int w, int h) {
            // Background
            g2.setColor(isDarkMode ? new Color(18, 22, 32) : new Color(240, 243, 248));
            g2.fillRect(0, 0, w, h);

            String building = buildingChoice.getSelectedItem();
            String floor = floorChoice.getSelectedItem();

            // Floor boundary dimensions
            int padX = 40;
            int padY = 35;
            int fw = w - (2 * padX);
            int fh = h - (2 * padY);

            // Architectural Outline & Foundation
            g2.setColor(isDarkMode ? new Color(30, 38, 54) : Color.WHITE);
            g2.fillRoundRect(padX, padY, fw, fh, 12, 12);
            g2.setColor(isDarkMode ? new Color(60, 75, 100) : new Color(180, 195, 215));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(padX, padY, fw, fh, 12, 12);

            // Central Walking Corridor
            int corridorY = padY + (fh / 2) - 25;
            int corridorH = 50;
            g2.setColor(isDarkMode ? new Color(24, 30, 44) : new Color(245, 247, 252));
            g2.fillRect(padX + 10, corridorY, fw - 20, corridorH);
            g2.setColor(isDarkMode ? new Color(45, 58, 80) : new Color(215, 225, 238));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f, 6.0f}, 0.0f));
            g2.drawLine(padX + 15, corridorY + 25, padX + fw - 15, corridorY + 25);

            // Corridor Label
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.setColor(isDarkMode ? new Color(120, 140, 170) : new Color(140, 155, 180));
            g2.drawString("◄ CENTRAL WALKING CORRIDOR & WAYFINDING PATHWAY ►", padX + (fw / 2) - 150, corridorY + 29);

            // Get Room boxes for this floor
            List<RoomBox> rooms = getRoomsForFloor(building, floor, padX, padY, fw, fh, corridorY, corridorH);

            for (RoomBox rb : rooms) {
                drawRoomBox(g2, rb);
            }

            // Legend at top
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.setColor(isDarkMode ? new Color(200, 220, 245) : new Color(30, 50, 85));
            g2.drawString(building.toUpperCase() + " — " + floor.toUpperCase() + " ARCHITECTURAL PLAN", padX + 12, padY - 12);

            // Legend indicators
            int lx = padX + fw - 280;
            int ly = padY - 14;
            drawLegendPill(g2, lx, ly, new Color(59, 130, 246), "Lecture");
            drawLegendPill(g2, lx + 65, ly, new Color(16, 185, 129), "Lab");
            drawLegendPill(g2, lx + 120, ly, new Color(139, 92, 246), "Office");
            drawLegendPill(g2, lx + 175, ly, new Color(245, 158, 11), "♿ Elevator");
            drawLegendPill(g2, lx + 255, ly, new Color(239, 68, 68), "Exit");
        }

        private void drawLegendPill(Graphics2D g2, int x, int y, Color c, String text) {
            g2.setColor(c);
            g2.fillRoundRect(x, y - 8, 8, 8, 2, 2);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(isDarkMode ? new Color(190, 205, 225) : new Color(60, 75, 95));
            g2.drawString(text, x + 11, y);
        }

        private void drawRoomBox(Graphics2D g2, RoomBox rb) {
            boolean isHover = (hoveredRoom == rb);
            boolean isSel = (selectedRoom == rb);

            // Fill
            Color baseColor = rb.color;
            if (isDarkMode) {
                baseColor = new Color(baseColor.getRed() / 2 + 20, baseColor.getGreen() / 2 + 20, baseColor.getBlue() / 2 + 30);
            }
            g2.setColor(baseColor);
            g2.fillRoundRect(rb.x, rb.y, rb.w, rb.h, 6, 6);

            // Highlight border
            if (isSel) {
                g2.setColor(new Color(255, 59, 48));
                g2.setStroke(new BasicStroke(2.5f));
            } else if (isHover) {
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(isDarkMode ? new Color(75, 90, 115) : new Color(190, 205, 225));
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(rb.x, rb.y, rb.w, rb.h, 6, 6);

            // Door notch facing corridor
            g2.setColor(isDarkMode ? new Color(24, 30, 44) : new Color(245, 247, 252));
            if (rb.doorOnBottom) {
                g2.fillRect(rb.x + (rb.w / 2) - 8, rb.y + rb.h - 3, 16, 4);
            } else {
                g2.fillRect(rb.x + (rb.w / 2) - 8, rb.y - 1, 16, 4);
            }

            // Labels
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int sx = rb.x + (rb.w - fm.stringWidth(rb.code)) / 2;
            g2.drawString(rb.code, sx, rb.y + 18);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            fm = g2.getFontMetrics();
            String label = rb.name;
            if (fm.stringWidth(label) > rb.w - 8) {
                while (label.length() > 3 && fm.stringWidth(label + "..") > rb.w - 8) {
                    label = label.substring(0, label.length() - 1);
                }
                label = label + "..";
            }
            int lx = rb.x + (rb.w - fm.stringWidth(label)) / 2;
            g2.drawString(label, lx, rb.y + 34);

            // Extra subtitle if room is tall
            if (rb.h > 55 && rb.sub != null && !rb.sub.isEmpty()) {
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                g2.setColor(new Color(220, 235, 255));
                fm = g2.getFontMetrics();
                int tx = rb.x + (rb.w - fm.stringWidth(rb.sub)) / 2;
                g2.drawString(rb.sub, tx, rb.y + 48);
            }
        }

        private List<RoomBox> getRoomsForFloor(String building, String floor, int px, int py, int fw, int fh, int cy, int ch) {
            List<RoomBox> list = new ArrayList<>();

            int topY = py + 8;
            int topH = cy - topY - 6;

            int bottomY = cy + ch + 6;
            int bottomH = py + fh - bottomY - 8;

            int availW = fw - 16;
            int colW = (availW - 140) / 4; // 4 major rooms per side + elevator/stairs at ends

            // Core Accessible Elevator & Stairs at Left & Right
            list.add(new RoomBox("♿ ELEVATOR", "Lift Core", "Accessible", px + 8, topY, 65, topH, true,
                new Color(217, 119, 6), "Accessible Lift • Multi-floor Wheelchair Access"));
            list.add(new RoomBox("STAIRS", "North Exit", "Emergency", px + 8, bottomY, 65, bottomH, false,
                new Color(220, 38, 38), "Fire Exit & Stairwell"));

            list.add(new RoomBox("RESTROOMS", "M/F Facilities", "Sanitation", px + fw - 75, topY, 65, topH, true,
                new Color(100, 116, 139), "Clean Restrooms & Drinking Water"));
            list.add(new RoomBox("EXIT", "South Stairs", "Emergency", px + fw - 75, bottomY, 65, bottomH, false,
                new Color(220, 38, 38), "South Fire Exit Stairwell"));

            // Middle Rooms based on Building & Floor
            int startX = px + 80;

            if (building.contains("Academic Block A")) {
                if (floor.contains("2nd")) {
                    list.add(new RoomBox("201", "Programming Lab", "100 i7 PCs", startX, topY, colW, topH, true,
                        new Color(16, 185, 129), "Room 201 • Programming Lab • Java / Python Practical Workstations"));
                    list.add(new RoomBox("204", "CSE HOD Office", "Faculty Staff", startX + colW + 8, topY, colW, topH, true,
                        new Color(139, 92, 246), "Room 204 • Computer Science Department Head & Faculty"));
                    list.add(new RoomBox("205", "AI & Robotics Lab", "GPU Cluster", startX + 2*(colW + 8), topY, colW, topH, true,
                        new Color(16, 185, 129), "Room 205 • High-Performance AI & Deep Learning Research Lab"));
                    list.add(new RoomBox("CSE-201", "Smart Classroom", "Smart Board", startX + 3*(colW + 8), topY, colW, topH, true,
                        new Color(59, 130, 246), "CSE-201 • 70 Seater Interactive Smart Lecture Hall"));

                    list.add(new RoomBox("LAB-01", "Coding Lab", "Competitive Tech", startX, bottomY, colW, bottomH, false,
                        new Color(16, 185, 129), "LAB-01 • Central Software Development Lab"));
                    list.add(new RoomBox("CSE-204", "Project Lab", "Final Year", startX + colW + 8, bottomY, colW, bottomH, false,
                        new Color(16, 185, 129), "CSE-204 • Capstone Projects & Prototyping"));
                    list.add(new RoomBox("SEM-201", "Seminar Hall", "Audio-Visual", startX + 2*(colW + 8), bottomY, 2*colW + 8, bottomH, false,
                        new Color(59, 130, 246), "Seminar Hall 201 • 150-capacity conference & workshop room"));
                } else if (floor.contains("1st")) {
                    list.add(new RoomBox("101", "1st Year Lecture", "Physics/Maths", startX, topY, colW, topH, true,
                        new Color(59, 130, 246), "Room 101 • First Year Core Engineering Classroom"));
                    list.add(new RoomBox("102", "Electronics Dept", "Lecture Hall", startX + colW + 8, topY, colW, topH, true,
                        new Color(59, 130, 246), "Room 102 • Electronics & Communication Theory Hall"));
                    list.add(new RoomBox("105", "Electronics Lab", "VLSI / Circuits", startX + 2*(colW + 8), topY, 2*colW + 8, topH, true,
                        new Color(16, 185, 129), "Room 105 • Hardware & Microcontroller Testing Facility"));

                    list.add(new RoomBox("FAC-101", "Faculty Cabins", "ECE Faculty", startX, bottomY, 2*colW + 8, bottomH, false,
                        new Color(139, 92, 246), "ECE Faculty Workstations & Consultation Rooms"));
                    list.add(new RoomBox("DRAW-01", "CAD Drawing Hall", "Drawing Tables", startX + 2*(colW + 8), bottomY, 2*colW + 8, bottomH, false,
                        new Color(59, 130, 246), "Engineering Graphics & Design Drafting Hall"));
                } else if (floor.contains("3rd")) {
                    list.add(new RoomBox("301", "Networking Lab", "Cisco Routers", startX, topY, colW, topH, true,
                        new Color(16, 185, 129), "Room 301 • Network Security & Infrastructure Testbed"));
                    list.add(new RoomBox("304", "IT Department", "HOD Office", startX + colW + 8, topY, colW, topH, true,
                        new Color(139, 92, 246), "Room 304 • Information Technology HOD Office"));
                    list.add(new RoomBox("LAB-02", "Cyber Lab", "Firewall Testing", startX + 2*(colW + 8), topY, 2*colW + 8, topH, true,
                        new Color(16, 185, 129), "LAB-02 • Cyber Defense & Ethical Hacking Lab"));

                    list.add(new RoomBox("IT-301", "Cloud Computing Lab", "AWS / GCP Cluster", startX, bottomY, 2*colW + 8, bottomH, false,
                        new Color(16, 185, 129), "IT-301 • Cloud Architectures & Distributed Systems Lab"));
                    list.add(new RoomBox("CONF-03", "Conference Room", "Smart Board", startX + 2*(colW + 8), bottomY, 2*colW + 8, bottomH, false,
                        new Color(59, 130, 246), "3rd Floor Executive Conference & Viva Room"));
                } else {
                    list.add(new RoomBox("G-01", "Main Auditorium Ent.", "Grand Hall", startX, topY, 2*colW + 8, topH, true,
                        new Color(59, 130, 246), "Ground Floor Entrance to Main Campus Auditorium"));
                    list.add(new RoomBox("G-02", "Student Cafeteria", "Snacks & Meals", startX + 2*(colW + 8), topY, 2*colW + 8, topH, true,
                        new Color(245, 158, 11), "Ground Floor Student Canteen & Lounge"));
                    list.add(new RoomBox("G-03", "Stationery & Xerox", "Print Shop", startX, bottomY, colW, bottomH, false,
                        new Color(100, 116, 139), "Bookstore, Photocopying & Stationery Counter"));
                    list.add(new RoomBox("G-04", "Medical Room", "First Aid Nurse", startX + colW + 8, bottomY, 3*colW + 16, bottomH, false,
                        new Color(220, 38, 38), "Campus First Aid Dispensary & Recovery Beds"));
                }
            } else {
                // Generic layout for other blocks
                list.add(new RoomBox("HALL-01", "Lecture Theatre 1", "Tiered Seating", startX, topY, 2*colW + 8, topH, true,
                    new Color(59, 130, 246), "Primary Lecture Theatre with Digital Projector"));
                list.add(new RoomBox("OFFICE-01", "Administrative Cell", "Inquiries", startX + 2*(colW + 8), topY, 2*colW + 8, topH, true,
                    new Color(139, 92, 246), "Departmental Coordination & Student Services"));
                list.add(new RoomBox("LAB-01", "Practical Work Area", "Workbenches", startX, bottomY, 2*colW + 8, bottomH, false,
                    new Color(16, 185, 129), "Applied Engineering Practical Lab"));
                list.add(new RoomBox("HALL-02", "Seminar & Meeting", "Smart Screen", startX + 2*(colW + 8), bottomY, 2*colW + 8, bottomH, false,
                    new Color(59, 130, 246), "Discussion & Group Project Work Area"));
            }

            return list;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (hoveredRoom != null) {
                selectedRoom = hoveredRoom;
                roomDetailsLabel.setText("📍 Selected: " + selectedRoom.code + " — " + selectedRoom.name + " (" + selectedRoom.sub + ")");
                roomDescLabel.setText(selectedRoom.desc);
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int mx = e.getX();
            int my = e.getY();
            int w = getWidth();
            int h = getHeight();
            int padX = 40, padY = 35, fw = w - 80, fh = h - 70;
            int corridorY = padY + (fh / 2) - 25, corridorH = 50;

            List<RoomBox> rooms = getRoomsForFloor(buildingChoice.getSelectedItem(), floorChoice.getSelectedItem(),
                                                  padX, padY, fw, fh, corridorY, corridorH);

            RoomBox prev = hoveredRoom;
            hoveredRoom = null;

            for (RoomBox rb : rooms) {
                if (mx >= rb.x && mx <= rb.x + rb.w && my >= rb.y && my <= rb.y + rb.h) {
                    hoveredRoom = rb;
                    break;
                }
            }

            if (hoveredRoom != null) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                roomDetailsLabel.setText("📍 Room " + hoveredRoom.code + ": " + hoveredRoom.name + " [" + hoveredRoom.sub + "]");
                roomDescLabel.setText(hoveredRoom.desc);
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            if (prev != hoveredRoom) {
                repaint();
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {
            hoveredRoom = null;
            repaint();
        }
        @Override public void mouseDragged(MouseEvent e) {}
    }

    /**
     * Helper struct for clickable room rectangles in floor plan.
     */
    private static class RoomBox {
        String code;
        String name;
        String sub;
        int x, y, w, h;
        boolean doorOnBottom;
        Color color;
        String desc;

        RoomBox(String code, String name, String sub, int x, int y, int w, int h,
                boolean doorOnBottom, Color color, String desc) {
            this.code = code;
            this.name = name;
            this.sub = sub;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.doorOnBottom = doorOnBottom;
            this.color = color;
            this.desc = desc;
        }
    }
}

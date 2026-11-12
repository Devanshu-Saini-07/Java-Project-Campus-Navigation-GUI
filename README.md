# Campus Navigation GUI — Java AWT Desktop Application

A comprehensive campus navigation desktop application built with **pure Java AWT** — zero external frameworks or heavyweight UI libraries required.  
Demonstrates robust **OOP design**, **$A^*$ & Dijkstra graph algorithms**, **turn-by-turn navigation**, **multi-role authentication**, **atomic file persistence**, and **automated testing**.

---

## ✨ Key Features

| Category | Capability |
|---|---|
| **Interactive Map** | Dynamic Canvas-based campus map with interactive nodes, clickable category filters, and live pulsing route animation. |
| **Intelligent Routing** | **$A^*$ (A-Star) & Dijkstra pathfinding** with Euclidean distance heuristic $h(n)$ for optimal path calculations. |
| **Turn-by-Turn Directions** | Relative vector angle calculation via `Math.atan2()` providing human-readable walking directions (straight, turn left/right, sharp turns, destination arrival). |
| **Accessibility Routing** | Wheelchair and ramp-friendly barrier-free routing filter that automatically bypasses stairs. |
| **Multimodal Shuttle** | Compares direct walking vs. taking campus shuttles to recommend bus stops when walking savings exceed threshold. |
| **Search & Aliases** | Real-time smart search supporting keywords and common abbreviations (`lib` → Library, `cse` → Computer Science, `admin` → Admin Block). |
| **Interactive Bus Directory** | View bus schedules, timings, and click any stop to instantly display and route to it on the campus map. |
| **Indoor Floor Plans** | Interactive multi-floor layouts for departments, laboratories, and administrative offices. |
| **Centralized Theming** | Single-source `CampusTheme` supporting 1-click Light Mode and high-contrast Dark Mode across all views. |
| **Security & Multi-Role RBAC** | SHA-256 + 16-byte random salt password hashing with dedicated role permissions (`ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`). Protected Admin Panel. |
| **Atomic File I/O** | `AtomicFileUtil` ensures zero data corruption on unexpected process termination using filesystem atomic swaps and automatic `.bak` backups. |
| **Automated Test Suite** | Standalone test harness (`TestRunner`) testing graph algorithms, pathfinding, persistence, and security with 100% test coverage. |

---

## 📁 Clean Project Structure

```text
Java Project Campus Navigation GUI/
├── src/
│   ├── main/
│   │   ├── java/com/campusnavigation/
│   │   │   ├── Main.java                        ← Application Entry Point
│   │   │   ├── gui/
│   │   │   │   ├── CampusNavigation.java        ← Main Frame & CardLayout Controller
│   │   │   │   ├── CampusMap.java               ← Canvas Map & Route Animation
│   │   │   │   ├── CampusTheme.java             ← Centralized Theme & Color Palette
│   │   │   │   ├── DashboardPanel.java          ← Main Navigation Dashboard
│   │   │   │   ├── LoginPanel.java              ← College Portal Login Interface
│   │   │   │   ├── IndoorFloorPlan.java         ← Building Floor Plans
│   │   │   │   ├── RouteFinderPanel.java        ← Route Selection & Turn Directions
│   │   │   │   ├── RoomFinderPanel.java         ← Room Search Directory
│   │   │   │   ├── AdminPanel.java              ← Role-Gated Admin Management
│   │   │   │   └── CustomDialog.java            ← Modal Dialogs
│   │   │   ├── model/
│   │   │   │   ├── CampusLocation.java          ← Location POJO
│   │   │   │   ├── CampusEdge.java              ← Pathway Edge POJO
│   │   │   │   ├── RoomInfo.java                ← Room POJO
│   │   │   │   ├── BusRoute.java                ← Bus Route POJO
│   │   │   │   ├── User.java                    ← User Account POJO
│   │   │   │   └── UserRole.java                ← Role Enum (STUDENT, FACULTY, ADMIN)
│   │   │   ├── repository/
│   │   │   │   ├── CampusDataManager.java       ← Central Data I/O & Indexing
│   │   │   │   ├── UserManager.java             ← Salted SHA-256 Authentication
│   │   │   │   ├── FavoriteManager.java         ← Favorites Persistence
│   │   │   │   ├── SearchHistory.java           ← Search History Persistence
│   │   │   │   └── AtomicFileUtil.java          ← Safe Atomic File Writing
│   │   │   └── service/
│   │   │       ├── CampusGraph.java             ← Adjacency List Graph Engine
│   │   │       └── RouteFinder.java             ← A* & Dijkstra Routing Engine
│   │   └── resources/
│   │       └── data/                            ← Default Fallback Data Files
│   └── test/
│       └── java/com/campusnavigation/
│           ├── CampusGraphTest.java             ← Graph Topology Unit Tests
│           ├── RouteFinderTest.java             ← A*, Accessible & Multimodal Tests
│           ├── CampusDataManagerTest.java       ← Search & Atomic I/O Tests
│           ├── UserAuthTest.java                ← SHA-256 & RBAC Tests
│           └── TestRunner.java                  ← Standalone Test Runner
├── bus_routes.txt                               ← Active Bus Routes
├── campus_data.txt                              ← Active Locations
├── campus_edges.txt                             ← Active Graph Edges
├── campus_rooms.txt                             ← Active Rooms
├── users.txt                                    ← Hashed & Salted User Accounts
└── README.md
```

---

## 🔑 Login Credentials

The system initializes with salted and SHA-256 hashed default accounts on first launch:

| Role | Username | Password | Access Level |
|---|---|---|---|
| **System Administrator** | `admin` | `123` | Full Access: Map, Routing, Bus Schedules, Admin Panel (CRUD) |
| **Student / Guest** | `student` | `student123` | Map, Pathfinding, Turn Directions, Bus Routes, Indoor Plans |
| **Faculty Member** | `faculty` | `faculty123` | Map, Departments, Staff Room Directory, Notices |

---

## ▶ How to Run in IntelliJ IDEA

1. Open this repository folder in **IntelliJ IDEA**.
2. IntelliJ automatically recognizes `src/main/java` as the **Sources Root**.
3. In the project tree, navigate to:  
   `src/main/java/com/campusnavigation/Main.java`
4. Click the **green play triangle ▶** in the gutter next to `public class Main` (or select `Run Campus Navigation (Main)` from the top toolbar).

---

## ▶ How to Compile & Run via Command Line (No Maven)

**Requirements:** Java JDK 8 or later.

```powershell
# 1. Compile all main application and test classes
javac -d bin -sourcepath "src/main/java;src/test/java" (Get-ChildItem -Recurse -Filter *.java "src\main\java","src\test\java" | ForEach-Object { $_.FullName })

# 2. Run the automated test suite (71 assertions)
java -cp bin com.campusnavigation.TestRunner

# 3. Launch the Desktop Application
java -cp bin com.campusnavigation.Main
```

---

## 🧪 Automated Testing

Run the zero-dependency unit test suite to verify core algorithms and security:
```bash
java -cp bin com.campusnavigation.TestRunner
```

Sample output:
```text
===============================================================
   CAMPUS NAVIGATION SYSTEM - AUTOMATED UNIT TEST SUITE
===============================================================
--- Running CampusGraphTest ---
  [PASS] testAddLocationAndRetrieve
  [PASS] testAddEdgeBidirectional
  [PASS] testEdgeAccessibilityAndPathType
  [PASS] testClearGraph

--- Running RouteFinderTest ---
  [PASS] testShortestPathAStar
  [PASS] testWheelchairAccessibleRoute
  [PASS] testUnreachableNodeHandling
  [PASS] testTurnByTurnDirectionsGeneration

--- Running CampusDataManagerTest ---
  [PASS] testSearchQueries
  [PASS] testAtomicFileWrite

--- Running UserAuthTest ---
  [PASS] testDefaultAccountsExist
  [PASS] testSuccessfulAuthentication
  [PASS] testFailedAuthentication
  [PASS] testPasswordHashingAndSalt
  [PASS] testRolePermissions
===============================================================
                     TEST EXECUTION SUMMARY
===============================================================
  Total Assertions Checked : 71
  Assertions Passed        : 71
  Assertions Failed        : 0
===============================================================
>>> RESULT: ALL TESTS PASSED SUCCESSFULLY (100%) <<<
```

---

## 🗺 Pathfinding & Algorithms

1. **$A^*$ Pathfinding:** Utilizes Euclidean spatial distance $h(n) = \sqrt{\Delta x^2 + \Delta y^2} \times 0.55$ as an admissible lower-bound heuristic to accelerate route discovery.
2. **Turn-by-Turn Navigation:** Analyzes direction vectors $(\vec{v}_1, \vec{v}_2)$ between consecutive path segments using $\theta = \text{atan2}(v_{2y}, v_{2x}) - \text{atan2}(v_{1y}, v_{1x})$ normalized to $[-180^\circ, 180^\circ]$ to produce accurate navigational cues.
3. **Multimodal Analysis:** Evaluates whether walking to an intermediate shuttle terminal saves net transit time compared to walking directly.
4. **Accessible Filtering:** Evaluates path edge accessibility flags (`isWheelchairAccessible`) to prevent routing through stairs or steep pathways when requested.

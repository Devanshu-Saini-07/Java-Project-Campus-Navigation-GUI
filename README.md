# Campus Navigation GUI — Java AWT Desktop Application

A comprehensive campus navigation system built with **pure Java AWT** — no Swing, no JavaFX, no external libraries.  
Designed as a college-level Java project demonstrating **OOP design**, **graph algorithms**, and **file persistence** in a GUI desktop app.

---

## ✨ Features

| Category | Feature |
|---|---|
| **Navigation** | Interactive visual campus map with clickable buildings |
| **Pathfinding** | Shortest path via **Dijkstra's algorithm** — source → destination with distance & walking time |
| **Search** | Smart case-insensitive search with aliases (`lib` → Library, `cse` → Computer Science) |
| **Room Finder** | Find rooms by code (e.g., `CSE-201`, `LAB-01`, `101`) |
| **Bus Routes** | Campus bus stop info, route numbers, timings, and nearby landmarks |
| **Favorites** | Save/view favourite locations, persisted to `favorites.txt` |
| **Search History** | Last 10 searches, persisted to `history.txt` |
| **Campus Timings** | Opening/closing hours for every campus location |
| **Emergency Contacts** | Sample emergency numbers clearly marked as demo data |
| **Admin Panel** | Full CRUD (Add/Update/Delete) on campus locations, saved to `campus_data.txt` |
| **File Storage** | Auto-creates all data files on first run with realistic sample data |
| **Dark Mode** | One-click toggle applies dark theme across the entire interface |
| **Error Handling** | AWT modal dialogs for invalid login, empty search, unknown location, same route endpoints |

---

## 🛠 Technologies

- **Language:** Java 8+ (standard library only)
- **GUI:** Java AWT — `Frame`, `Panel`, `Canvas`, `CardLayout`, `BorderLayout`, `GridLayout`, `FlowLayout`
- **Algorithm:** Dijkstra's Shortest Path (priority queue, adjacency list graph)
- **Storage:** Flat text files with `|` delimiter
- **Concurrency:** `EventQueue.invokeLater` for AWT thread safety; `synchronized` file managers

---

## 📁 Project Structure

```
Campus Navigation GUI/
│
├── CampusNavigation.java      ← Main entry point (Frame, CardLayout LOGIN↔DASHBOARD)
├── LoginPanel.java            ← AWT login screen (credentials + Enter-key support)
├── DashboardPanel.java        ← Main dashboard (sidebar, top bar, card content area)
│
├── CampusMap.java             ← Canvas-based interactive map (paint, mouse click)
├── RouteFinderPanel.java      ← Route finder UI (dropdowns + Dijkstra result display)
├── RoomFinderPanel.java       ← Room search UI (e.g., CSE-201, LAB-01)
├── AdminPanel.java            ← CRUD admin UI for managing locations
│
├── CampusLocation.java        ← Location POJO (id, name, category, coordinates, etc.)
├── CampusEdge.java            ← Edge POJO (sourceId, destId, distance in metres)
├── RoomInfo.java              ← Room POJO (room number, building, floor, department)
├── BusRoute.java              ← Bus route POJO (stop, route number, destination, timings)
│
├── CampusGraph.java           ← Adjacency list graph structure
├── RouteFinder.java           ← Dijkstra's algorithm + RouteResult (path, distance, time)
│
├── CampusDataManager.java     ← Central manager: file I/O, search, CRUD, emergency contacts
├── FavoriteManager.java       ← Manages favorites.txt (add/remove/view)
├── SearchHistory.java         ← Manages history.txt (last 10 searches)
│
├── CustomDialog.java          ← Pure AWT modal dialogs (info / error)
│
├── campus_data.txt            ← Location data (auto-created on first run)
├── campus_edges.txt           ← Graph edges/paths (auto-created on first run)
├── campus_rooms.txt           ← Room directory (auto-created on first run)
├── bus_routes.txt             ← Bus route data (auto-created on first run)
├── favorites.txt              ← User favourites (auto-created on first run)
└── history.txt                ← Search history (auto-created on first run)
```

---

## ▶ How to Compile & Run

**Requirements:** JDK 8 or later installed, `javac` and `java` on your PATH.

```bash
# 1. Navigate to the project folder
cd "Java Project Campus Navigation GUI"

# 2. Compile (compiles all .java files automatically)
javac CampusNavigation.java

# 3. Run
java CampusNavigation
```

> **Note:** All data files (`campus_data.txt`, `favorites.txt`, etc.) are created automatically on the first run. No manual setup required.

---

## 🔑 Demo Login

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `123` |

---

## 🗺 Algorithms Used

### Dijkstra's Shortest Path Algorithm
Implemented in `RouteFinder.java` on the campus graph:

1. **Graph Model:** Locations are nodes; walkable paths are undirected weighted edges (distance in metres).
2. **Priority Queue:** A min-heap (`PriorityQueue`) always processes the closest unvisited node.
3. **Relaxation:** For each neighbour, if `dist[current] + edge.weight < dist[neighbour]`, update distance and track predecessor.
4. **Path Backtrack:** After reaching the target, follow the `predecessors` map backwards to reconstruct the full path.
5. **Walking Time:** Estimated at ~70 metres/minute. `ceil(totalDistance / 70)` gives minutes.

**Time Complexity:** O((V + E) log V) where V = locations, E = edges.

---

## 💾 File Storage Format

All data files use `|` as a field delimiter. They are auto-created with sample campus data if missing.

| File | Format |
|---|---|
| `campus_data.txt` | `id\|name\|category\|building\|floor\|description\|nearby\|timings\|x\|y\|aliases` |
| `campus_edges.txt` | `sourceId\|destinationId\|distance` |
| `campus_rooms.txt` | `roomNumber\|building\|floor\|departmentOrLab\|description` |
| `bus_routes.txt` | `stopName\|routeNumber\|destination\|timings\|nearbyLocations` |
| `favorites.txt` | One location ID per line |
| `history.txt` | One search query per line (newest last, max 10) |

---

## 🎓 Sample Data Included

- **23 campus locations** — Main Gate, Admin Block, Academic Blocks A & B, Library, Auditorium, 5 Departments, 4 Labs, 4 Hostels, Sports Complex, 3 Bus Stops
- **28 graph edges** — connecting all major locations with realistic walking distances
- **16 rooms** — including `101`, `CSE-201`, `CSE-204`, `LAB-01`, `LAB-02`, `ADM-01`, etc.
- **6 bus routes** — with stop names, route numbers, destinations, and timings

---

## 🔍 Smart Search Aliases

| You type | Resolves to |
|---|---|
| `lib` | Library |
| `cse` | Computer Science Department |
| `admin` | Admin Block |
| `hostel` | All hostels |

---

## 🚀 Future Scope

- Integrate a real college campus map image as a background
- User account management with multiple login profiles
- Export route directions as a PDF or text file
- Notification/reminder system for class timings
- Accessibility features (keyboard-only navigation, high-contrast mode)
- REST API backend for live data updates

---

## ⚠ Disclaimer

Emergency contact numbers displayed in the app are **demo/sample data only** and should not be used in an actual emergency. Replace them with your institution's real contacts before deployment.

---

*Built with Java AWT — demonstrating that you don't need modern frameworks to build a fully functional GUI application.*

/**
 * Main application entry point for running directly from IntelliJ IDEA or command line.
 * Forwards execution to com.campusnavigation.Main on the AWT Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        com.campusnavigation.Main.main(args);
    }
}

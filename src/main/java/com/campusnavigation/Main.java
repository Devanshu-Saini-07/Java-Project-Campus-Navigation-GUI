package com.campusnavigation;

import java.awt.EventQueue;
import com.campusnavigation.gui.CampusNavigation;

/**
 * Application entry point. Starts the GUI on the AWT Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        // All UI work must run on the EDT
        EventQueue.invokeLater(() -> new CampusNavigation());
    }
}

package com.campusnavigation.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized Design System for the Campus Navigation Application.
 * Provides unified color palettes, typography, and contrast rules for Light and Dark modes.
 */
public final class CampusTheme {

    private CampusTheme() {}

    // =========================================================================
    // Core Backgrounds
    // =========================================================================
    public static Color getBackground(boolean dark) {
        return dark ? new Color(20, 24, 33) : new Color(245, 247, 250);
    }

    public static Color getTopBg(boolean dark) {
        return dark ? new Color(24, 28, 38) : new Color(240, 244, 250);
    }

    public static Color getSidebarBg(boolean dark) {
        return dark ? new Color(18, 22, 30) : new Color(235, 240, 248);
    }

    public static Color getCardBg(boolean dark) {
        return dark ? new Color(34, 40, 54) : Color.WHITE;
    }

    public static Color getCardHoverBg(boolean dark) {
        return dark ? new Color(42, 50, 68) : new Color(240, 246, 255);
    }

    public static Color getHeaderBg(boolean dark) {
        return dark ? new Color(28, 33, 45) : new Color(235, 241, 250);
    }

    public static Color getMapBarBg(boolean dark) {
        return dark ? new Color(26, 32, 44) : new Color(230, 238, 248);
    }

    public static Color getStatusBg(boolean dark) {
        return dark ? new Color(18, 22, 30) : new Color(220, 225, 235);
    }

    public static Color getSectionHeaderBg(boolean dark) {
        return dark ? new Color(25, 30, 42) : new Color(225, 232, 244);
    }

    // =========================================================================
    // Text & Foreground Colors
    // =========================================================================
    public static Color getTextPrimary(boolean dark) {
        return dark ? new Color(235, 242, 255) : new Color(25, 45, 85);
    }

    public static Color getTextSecondary(boolean dark) {
        return dark ? new Color(165, 180, 205) : new Color(75, 90, 115);
    }

    public static Color getTextMuted(boolean dark) {
        return dark ? new Color(125, 140, 165) : new Color(110, 125, 145);
    }

    public static Color getSectionLabelFg(boolean dark) {
        return dark ? new Color(150, 170, 200) : new Color(85, 100, 125);
    }

    // =========================================================================
    // Component Colors
    // =========================================================================
    public static Color getSidebarButtonBg(boolean dark) {
        return dark ? new Color(34, 40, 54) : new Color(248, 250, 254);
    }

    public static Color getSidebarButtonFg(boolean dark) {
        return dark ? new Color(220, 230, 245) : new Color(25, 35, 60);
    }

    public static Color getInputBg(boolean dark) {
        return dark ? new Color(38, 44, 58) : Color.WHITE;
    }

    public static Color getInputFg(boolean dark) {
        return dark ? Color.WHITE : Color.BLACK;
    }

    // Accent Colors
    public static Color getAccentBlue() {
        return new Color(35, 120, 215);
    }

    public static Color getAccentGreen() {
        return new Color(50, 135, 75);
    }

    public static Color getAccentCrimson() {
        return new Color(195, 60, 60);
    }

    public static Color getAccentAmber() {
        return new Color(217, 119, 6);
    }

    public static Color getAccentPurple() {
        return new Color(139, 92, 246);
    }

    public static Color getAccentRoute() {
        return new Color(255, 87, 34); // Vibrant Red-Orange
    }

    // Category Node Colors
    public static Color getCategoryColor(String category, boolean dark) {
        String cat = (category != null) ? category.toLowerCase() : "";
        if (cat.contains("building") || cat.contains("admin")) {
            return dark ? new Color(30, 80, 140) : new Color(50, 115, 190);
        } else if (cat.contains("hostel")) {
            return dark ? new Color(100, 50, 130) : new Color(140, 80, 170);
        } else if (cat.contains("lab")) {
            return dark ? new Color(140, 80, 20) : new Color(200, 110, 30);
        } else if (cat.contains("department")) {
            return dark ? new Color(20, 110, 90) : new Color(30, 145, 115);
        } else if (cat.contains("bus")) {
            return dark ? new Color(140, 115, 20) : new Color(195, 150, 25);
        } else if (cat.contains("facility") || cat.contains("sports")) {
            return dark ? new Color(40, 110, 50) : new Color(50, 145, 70);
        } else if (cat.contains("entry") || cat.contains("gate")) {
            return dark ? new Color(110, 50, 60) : new Color(170, 70, 80);
        } else {
            return dark ? new Color(70, 75, 85) : new Color(110, 120, 130);
        }
    }

    // =========================================================================
    // Typography
    // =========================================================================
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_SUBHEADER = new Font("Arial", Font.BOLD, 12);
    public static final Font FONT_BODY = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_CAPTION = new Font("Arial", Font.PLAIN, 11);
    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 10);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);
}

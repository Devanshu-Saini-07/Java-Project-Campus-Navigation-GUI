package com.campusnavigation.model;

public enum UserRole {
    ROLE_STUDENT("Student / Guest", "Standard campus wayfinding, room discovery, and bus schedule access"),
    ROLE_FACULTY("Faculty / Staff", "Academic staff access, faculty cabins, department directories"),
    ROLE_ADMIN("Administrator", "Full system privileges, location management, and infrastructure editing");

    private final String title;
    private final String description;

    UserRole(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public boolean canManageLocations() {
        return this == ROLE_ADMIN;
    }

    public boolean canAccessAdminPanel() {
        return this == ROLE_ADMIN;
    }
}

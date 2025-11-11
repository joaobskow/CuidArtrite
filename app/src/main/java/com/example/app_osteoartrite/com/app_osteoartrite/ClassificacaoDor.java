package com.example.app_osteoartrite.com.app_osteoartrite;

public class ClassificacaoDor {
    private String title;
    private String description;
    private boolean expanded;

    public ClassificacaoDor(String title, String description) {
        this.title = title;
        this.description = description;
        this.expanded = false; // Começa fechado
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
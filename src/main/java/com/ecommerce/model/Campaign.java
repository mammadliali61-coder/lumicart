package com.ecommerce.model;

public class Campaign {
    private final String title;
    private final String headline;
    private final String description;
    private final boolean primary;

    public Campaign(String title, String headline, String description, boolean primary) {
        this.title = title;
        this.headline = headline;
        this.description = description;
        this.primary = primary;
    }

    public String getTitle() {
        return title;
    }

    public String getHeadline() {
        return headline;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrimary() {
        return primary;
    }
}

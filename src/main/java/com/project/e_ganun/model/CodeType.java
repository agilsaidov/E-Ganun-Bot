package com.project.e_ganun.model;

public enum CodeType {
    INZIBATI_XETALAR("İnzibati Xətalar Məcəlləsi"),
    CINAYET("Cinayət Məcəlləsi");

    private final String displayName;

    CodeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

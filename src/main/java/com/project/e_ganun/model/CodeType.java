package com.project.e_ganun.model;

import lombok.Getter;

@Getter
public enum CodeType {
    INZIBATI_XETALAR("İnzibati Xətalar Məcəlləsi"),
    CINAYET("Cinayət Məcəlləsi"),
    KONSTITUSIYA("Azərbaycan Respublikası Konstitusiyası");

    private final String displayName;

    CodeType(String displayName) {
        this.displayName = displayName;
    }

}

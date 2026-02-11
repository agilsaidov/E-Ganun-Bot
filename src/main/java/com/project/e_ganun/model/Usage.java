package com.project.e_ganun.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Table(name = "usage", schema = "ganun")
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "total_searches")
    private Integer totalSearches;

    @Column(name = "last_search_date")
    private LocalDateTime lastSearchDate;

    @Column(name = "last_search_query")
    private String lastSearchQuery;

    @Column(name = "last_search_code")
    private String lastSearchCode;

    @Column(name = "total_messages")
    private Integer totalMessages;

    @Column(name = "first_usage_date")
    private LocalDateTime firstUsageDate;

    @PrePersist
    protected void onCreate() {
        if (totalSearches == null) {
            totalSearches = 0;
        }
        if (totalMessages == null) {
            totalMessages = 0;
        }
        if (firstUsageDate == null) {
            firstUsageDate = LocalDateTime.now();
        }
    }
}

package com.project.e_ganun.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "law", schema = "ganun")
@AllArgsConstructor @NoArgsConstructor
@IdClass(LawId.class)
public class Law {
    @Id
    @Column(name = "law_no")
    private String lawNo;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type")
    private CodeType codeType;

    @Column(name = "law_text",columnDefinition = "TEXT")
    private String lawText;
}

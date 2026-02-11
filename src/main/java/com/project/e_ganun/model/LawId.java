package com.project.e_ganun.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LawId implements Serializable {
    private String lawNo;
    private CodeType codeType;
}
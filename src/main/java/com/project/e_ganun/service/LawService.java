package com.project.e_ganun.service;

import com.project.e_ganun.model.Law;
import com.project.e_ganun.model.LawId;
import com.project.e_ganun.repository.LawRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class LawService {

    private final LawRepo lawRepo;

    public List<Law> searchByLawId(LawId lawId) {
        return lawRepo.findByLawNoAndCodeType(lawId.getLawNo(), lawId.getCodeType().name());
    }
}

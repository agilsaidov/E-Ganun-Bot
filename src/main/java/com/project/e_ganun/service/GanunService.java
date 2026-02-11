package com.project.e_ganun.service;

import com.project.e_ganun.model.Law;
import com.project.e_ganun.repository.LawRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class GanunService {

    private final LawRepo lawRepo;

    public List<Law> searchByGanunNo(String ganunNo) {
        return lawRepo.findByGanunNoStartingWith(ganunNo);
    }

    public Optional<Law> getExactGanun(String ganunNo) {
        return lawRepo.findByExactGanunNo(ganunNo);
    }
}

package com.project.e_ganun.repository;

import com.project.e_ganun.model.Law;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LawRepo extends CrudRepository<Law, String> {

    @Query(value = "SELECT * FROM ganun.law WHERE law_no LIKE :lawNo || '.%'",
            nativeQuery = true)
    List<Law> findByGanunNoStartingWith(@Param("lawNo") String lawNo);

    @Query(value = "SELECT * FROM ganun.law WHERE ganun_no = :lawNo",
            nativeQuery = true)
    Optional<Law> findByExactGanunNo(@Param("lawNo") String lawNo);
}

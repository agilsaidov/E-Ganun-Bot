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

    @Query(value = "SELECT * FROM ganun.law WHERE code_type = :codeType AND law_no LIKE CONCAT(:lawNo, '.%') ORDER BY law_no",
            nativeQuery = true)
    List<Law> findByLawNoAndCodeType(@Param("lawNo") String lawNo, @Param("codeType") String codeType);
}

package org.ilyatyamin.yacontesthelper.grades.repository;

import org.ilyatyamin.yacontesthelper.grades.dao.GradesResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradesResultRepository extends JpaRepository<GradesResult, Long> {
}

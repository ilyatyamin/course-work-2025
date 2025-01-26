package org.ilyatyamin.yacontesthelper.repository;

import org.ilyatyamin.yacontesthelper.dao.GradesResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradesResultRepository extends JpaRepository<GradesResult, Long> {
}

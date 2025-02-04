package org.ilyatyamin.yacontesthelper.repository;

import org.ilyatyamin.yacontesthelper.dao.UpdateTaskDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateTaskRepository extends JpaRepository<UpdateTaskDao, Long> {
}

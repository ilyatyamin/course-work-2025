package org.ilyatyamin.yacontesthelper.autoupdate.repository;

import org.ilyatyamin.yacontesthelper.autoupdate.dao.UpdateTaskDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateTaskRepository extends JpaRepository<UpdateTaskDao, Long> {
}

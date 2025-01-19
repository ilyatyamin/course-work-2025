package org.ilyatyamin.yacontesthelper.repository;

import org.ilyatyamin.yacontesthelper.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDao, Long> {
}

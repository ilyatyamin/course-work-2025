package org.ilyatyamin.yacontesthelper.security.repository;

import org.ilyatyamin.yacontesthelper.security.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDao, Long> {
}

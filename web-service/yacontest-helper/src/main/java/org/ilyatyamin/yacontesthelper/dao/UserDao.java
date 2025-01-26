package org.ilyatyamin.yacontesthelper.dao;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "users")
@Entity
@Data
public class UserDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;
}

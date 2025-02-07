package org.ilyatyamin.yacontesthelper.autoupdate.dao;

import jakarta.persistence.*;
import lombok.Data;

import javax.annotation.Nullable;

@Table(name = "update_task")
@Entity
@Data
public class UpdateTaskDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private String cronExpression;

    private String updateUrl;

    @Nullable
    private String credentialsForUpdate;
}

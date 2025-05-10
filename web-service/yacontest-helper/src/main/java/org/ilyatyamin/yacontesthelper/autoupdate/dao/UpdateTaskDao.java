package org.ilyatyamin.yacontesthelper.autoupdate.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Table(name = "update_task")
@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UpdateTaskDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private Long taskId;

    private String cronExpression;

    private String updateUrl;

    @Nullable
    @Column(columnDefinition="TEXT")
    private String credentialsForUpdate;

    @Nullable
    @Column(columnDefinition="TEXT")
    private String initialRequest;

    private TaskStatus status;
}

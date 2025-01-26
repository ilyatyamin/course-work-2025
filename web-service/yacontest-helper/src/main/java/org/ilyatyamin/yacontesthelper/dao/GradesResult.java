package org.ilyatyamin.yacontesthelper.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades_result")
@Getter
@NoArgsConstructor
public class GradesResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime createdAt;

    @Column(columnDefinition="TEXT")
    private String payload;

    public GradesResult(Long userId, String payload) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.payload = payload;
    }
}

package org.ilyatyamin.yacontesthelper.dao;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Map<String, Double>> payload;

    public GradesResult(Long userId, Map<String, Map<String, Double>> payload) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.payload = payload;
    }
}

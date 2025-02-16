package org.ilyatyamin.yacontesthelper.security.dao;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class TokenDao {
    public TokenDao(final Long userId, final String payload,
                    final TokenType tokenType, final Date expiresAt) {
        this.userId = userId;
        this.payload = payload;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @JoinColumn(table = "users", referencedColumnName = "id")
    private Long userId;

    @Column(name = "payload", unique = true, nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

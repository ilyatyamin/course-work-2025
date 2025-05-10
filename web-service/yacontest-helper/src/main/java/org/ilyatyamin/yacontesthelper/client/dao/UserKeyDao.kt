package org.ilyatyamin.yacontesthelper.client.dao

import jakarta.persistence.*
import org.ilyatyamin.yacontesthelper.client.enums.KeyType
import java.time.LocalDateTime

@Table(name = "user_key")
@Entity
data class UserKeyDao(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id")
    var userId: Long? = null,

    @Column(name = "key", nullable = false)
    var key: String? = null,

    @Column(name = "description", nullable = false)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: KeyType? = null,

    @Column(name = "created", nullable = false)
    var created: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated", nullable = false)
    var updated: LocalDateTime = LocalDateTime.now(),
)
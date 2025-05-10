package org.ilyatyamin.yacontesthelper.client.repository

import org.ilyatyamin.yacontesthelper.client.dao.UserKeyDao
import org.ilyatyamin.yacontesthelper.client.enums.KeyType
import org.springframework.data.jpa.repository.JpaRepository

interface UserKeyRepository: JpaRepository<UserKeyDao, Long> {
    fun getAllByUserId(userId: Long): List<UserKeyDao>
    fun getAllByUserIdAndType(userId: Long, type: KeyType): List<UserKeyDao>
}
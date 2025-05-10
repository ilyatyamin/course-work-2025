package org.ilyatyamin.yacontesthelper.autoupdate.repository

import org.ilyatyamin.yacontesthelper.autoupdate.dao.UpdateTaskDao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UpdateTaskRepository : JpaRepository<UpdateTaskDao, Long> {
    fun findAllByOwnerId(ownerId: Long): List<UpdateTaskDao>
}

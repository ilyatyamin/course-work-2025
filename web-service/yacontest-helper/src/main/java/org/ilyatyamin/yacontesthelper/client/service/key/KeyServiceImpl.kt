package org.ilyatyamin.yacontesthelper.client.service.key

import org.ilyatyamin.yacontesthelper.client.dao.UserKeyDao
import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysRequest
import org.ilyatyamin.yacontesthelper.client.dto.KeyInfo
import org.ilyatyamin.yacontesthelper.client.repository.UserKeyRepository
import org.ilyatyamin.yacontesthelper.client.service.CommonClientService
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.stereotype.Service

@Service
class KeyServiceImpl(
    private val userKeyRepository: UserKeyRepository,
    userService: UserService,
) : KeyService, CommonClientService(userService) {
    override fun getAllKeys(request: GetAllUserKeysRequest): List<KeyInfo> = clientSecurityCheck(request.username) {
        val userId = userService.getByUsername(request.username).id

        val infoList: List<UserKeyDao> = if (request.type == null) {
            userKeyRepository.getAllByUserId(userId)
        } else {
            userKeyRepository.getAllByUserIdAndType(userId, request.type)
        }

        infoList.map { KeyInfo(key = it.key, type = it.type, description = it.description) }
    }
}
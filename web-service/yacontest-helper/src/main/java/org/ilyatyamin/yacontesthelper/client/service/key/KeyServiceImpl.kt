package org.ilyatyamin.yacontesthelper.client.service.key

import org.ilyatyamin.yacontesthelper.client.dao.UserKeyDao
import org.ilyatyamin.yacontesthelper.client.dto.AddNewKeyRequest
import org.ilyatyamin.yacontesthelper.client.dto.DeleteKeyRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysRequest
import org.ilyatyamin.yacontesthelper.client.dto.KeyInfo
import org.ilyatyamin.yacontesthelper.client.repository.UserKeyRepository
import org.ilyatyamin.yacontesthelper.client.service.CommonClientService
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.http.HttpStatus
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

        infoList.map {
            KeyInfo(
                id = it.id, key = it.key,
                type = it.type, description = it.description
            )
        }
    }

    override fun addNewKey(request: AddNewKeyRequest): Unit = clientSecurityCheck(request.username) {
        val userId = userService.getByUsername(request.username).id

        userKeyRepository.save(
            UserKeyDao(
                userId = userId,
                key = request.key,
                type = request.type,
                description = request.description,
            )
        )
    }

    override fun deleteKey(request: DeleteKeyRequest) = clientSecurityCheck(request.username){
        val userId = userService.getByUsername(request.username).id
        val keyDao = userKeyRepository.findById(userId)

        if (keyDao.isEmpty) {
            throw YaContestException(
                code = HttpStatus.NOT_FOUND.value(),
                message = ExceptionMessages.KEY_NOT_FOUND.message
            )
        } else if (keyDao.get().userId != userId) {
            throw YaContestException(
                code = HttpStatus.FORBIDDEN.value(),
                message = ExceptionMessages.FORBIDDEN.message
            )
        }

        userKeyRepository.delete(keyDao.get())
    }
}
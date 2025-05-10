package org.ilyatyamin.yacontesthelper.client.service.key

import org.ilyatyamin.yacontesthelper.client.dto.AddNewKeyRequest
import org.ilyatyamin.yacontesthelper.client.dto.DeleteKeyRequest
import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysRequest
import org.ilyatyamin.yacontesthelper.client.dto.KeyInfo

interface KeyService {
    fun getAllKeys(request: GetAllUserKeysRequest): List<KeyInfo>

    fun addNewKey(request: AddNewKeyRequest)

    fun deleteKey(request: DeleteKeyRequest)
}
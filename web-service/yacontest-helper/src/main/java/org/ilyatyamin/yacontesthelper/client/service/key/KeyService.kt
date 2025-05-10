package org.ilyatyamin.yacontesthelper.client.service.key

import org.ilyatyamin.yacontesthelper.client.dto.GetAllUserKeysRequest
import org.ilyatyamin.yacontesthelper.client.dto.KeyInfo

interface KeyService {
    fun getAllKeys(request: GetAllUserKeysRequest): List<KeyInfo>
}
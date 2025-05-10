package org.ilyatyamin.yacontesthelper.client.dto

import org.ilyatyamin.yacontesthelper.client.enums.KeyType

data class KeyInfo(
    val id: Long?,
    val key: String?,
    val type: KeyType?,
    val description: String?,
)

data class GetAllUserKeysRequest(
    val username: String,
    val type: KeyType?
)

data class GetAllUserKeysResponse(
    val keys: List<KeyInfo>,
)

data class AddNewKeyRequest(
    val username: String,
    val key: String,
    val type: KeyType,
    val description: String,
)

data class SuccessResponse(
    val success: Boolean,
)

data class DeleteKeyRequest(
    val username: String,
    val keyId: Long
)
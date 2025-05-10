package org.ilyatyamin.yacontesthelper.client.service

import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse

interface ClientService {
    fun getUserInfo(username: String): GetUserInfoResponse
}
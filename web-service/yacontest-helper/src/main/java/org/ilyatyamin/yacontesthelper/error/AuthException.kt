package org.ilyatyamin.yacontesthelper.error

class AuthException(val code: Int, message: String) : RuntimeException(message) {
}
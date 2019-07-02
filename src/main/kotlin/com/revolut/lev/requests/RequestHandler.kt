package com.revolut.lev.requests

interface RequestHandler<T> {

    fun process(request: T?, queryParamsMap: Map<String, String>): Answer

    fun validate(request: T?, queryParamsMap:  Map<String, String>): String {
        return ""
    }

    fun deserialize(value: String): T? {
        return null
    }

}
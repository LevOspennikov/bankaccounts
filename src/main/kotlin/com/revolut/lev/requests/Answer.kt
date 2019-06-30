package com.revolut.lev.requests

class Answer(val httpCode: Int = 200) {
    var response: String = ""

    constructor(httpCode: Int , resp: String ): this(httpCode) {
        if (httpCode in 200..299) {
            this.response = """{ 
                |   "result": ${if (resp[0] == '[') resp else "\"$resp\""}
                |}""".trimMargin()
        } else {
            this.response = """{ 
                |   "error": "$resp"
                |}""".trimMargin()
        }
    }
}
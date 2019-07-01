package com.revolut.lev.requests

import com.revolut.lev.dao.AccountDao
import com.revolut.lev.dao.LedgerDao
import spark.Request
import spark.Response
import spark.Route

abstract class AbstractRequestHandler<T>(
    protected val accountDao: AccountDao,
    protected val ledgerDao: LedgerDao
) :
    RequestHandler<T>, Route {

    abstract override fun process(request: T?, queryParamsMap: Map<String, String>): Answer

    override fun handle(request: Request, response: Response): Any {
        var value: T? = null
        try {
            value = this.deserialize(request.body())
        } catch (err: Exception) {
            response.status(400)
            response.body("Can't parse request $request")
            return "Can't parse request ${request.body()}"
        }

        val queryParams = request.params()

        val error = validate(value, queryParams)
        val answer = if (error != "") {
            Answer(400, error)
        } else {
            process(value, queryParams)
        }

        response.status(answer.httpCode)
        response.type("application/json")
        response.body(answer.response)
        return answer.response
    }

}
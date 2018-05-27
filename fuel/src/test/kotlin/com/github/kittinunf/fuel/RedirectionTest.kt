package com.github.kittinunf.fuel

import com.github.kittinunf.fuel.core.*
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import javax.net.ssl.HttpsURLConnection
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class RedirectionTest : BaseTestCase() {
    private val manager: FuelManager by lazy {
        FuelManager().apply {
            basePath = "https://httpstat.us"
        }
    }

    @Test
    fun httpRedirection() {
        var request: Request? = null
        var response: Response? = null
        var redirectLocation: String? = null
        var data: Any? = null
        var error: FuelError? = null

        val headerKey = "Custom"
        val headerValue = "foobar"

        manager.request(Method.GET, "/303").header(headerKey to headerValue).response { req, res, result ->
            request = req
            response = res

            val (d, err) = result
            data = d
            error = err
            
            redirectionLocation = response.httpResponseHeaders["Location"]
        }

        assertThat(request, notNullValue())
        assertThat(response, notNullValue())
        assertThat(error, nullValue())
        assertThat(data, notNullValue())
        assertThat(redirectionLocation, "https://httpstat.us")

        val statusCode = HttpsURLConnection.HTTP_SEE_OTHER
        assertThat(response?.statusCode, isEqualTo(statusCode))

        val string = String(data as ByteArray)
        assertThat(string, containsString(headerKey))
        assertThat(string, containsString(headerValue))
    }

}

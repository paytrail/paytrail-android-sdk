package fi.paytrail.paymentsdk.test.server

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.IOException
import java.io.InputStreamReader

enum class RequestMethod {
    GET,
    PUT,
    POST,
    DELETE,
    PATCH,
}

data class RequestKey(val method: RequestMethod, val path: String) {
    constructor(method: String, path: String) : this(RequestMethod.valueOf(method), path)
}

data class Response(
    val code: Int,
    val body: String,
    val headers: Map<String, String>,
)

fun responseFromTape(code: Int, tapeName: String): Response {
    val tape = getTapeFileContent(tapeName)
    return Response(code, tape.body, tape.headers)
}

fun responseFromString(
    code: Int,
    body: String,
    headers: Map<String, String> = emptyMap(),
): Response = Response(code, body, headers)

infix fun Int.withBodyFrom(file: String) = responseFromTape(this, file)
infix fun Int.withBody(body: String) = responseFromString(this, body)

infix fun Response.withHeader(header: Pair<String, String>): Response {
    return Response(code, body, headers + header)
}

class TapeAssetDispatcher : Dispatcher() {

    val requests = mutableListOf<RecordedRequest>()
    private val responses = mutableMapOf<RequestKey, Response>()
    private val tokens = mutableMapOf<String, () -> String>()
    private val additionalHeaders = mutableMapOf<String, (Map<String, String>, String) -> String>()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val key = RequestKey(request.method!!, request.path!!)
        val response = responses[key]
        requests += request
        return response?.let {
            val body = tokens.entries.fold(it.body) { acc, (token, value) ->
                acc.replace("{{$token}}", value())
            }
            MockResponse().setResponseCode(it.code).setBody(body).apply {
                it.headers.forEach { header -> setHeader(header.key, header.value) }
                additionalHeaders.forEach { (name, valueFun) ->
                    setHeader(name, valueFun(it.headers, body))
                }
            }
        } ?: MockResponse().setResponseCode(400)
    }

    operator fun invoke(block: TapeAssetDispatcher.() -> Unit) {
        block.invoke(this)
    }

    infix fun RequestMethod.requestOf(path: String) = RequestKey(this, path)

    infix fun RequestKey.serves(file: String) {
        responses[this] = responseFromTape(200, file)
    }

    infix fun RequestKey.serves(response: Response) {
        responses[this] = response
    }

    infix fun String.bodyTokenReplacedBy(tokenValue: String) =
        this.bodyTokenReplacedBy { tokenValue }

    infix fun String.bodyTokenReplacedBy(tokenValue: () -> String) {
        tokens[this] = tokenValue
    }

    infix fun String.globalHeader(tokenValue: (Map<String, String>, String) -> String) {
        additionalHeaders[this] = tokenValue
    }
}

private fun getTapeFileContent(fileName: String): Tape {
    val context = InstrumentationRegistry.getInstrumentation().context
    val bodyFile = "tapes/$fileName.body"
    val headersFile = "tapes/$fileName.headers"
    val body = try {
        InputStreamReader(context.assets.open(bodyFile)).use { it.readText() }
    } catch (e: IOException) {
        Log.i("TapeAssetDispatcher", "Could not open body file: $bodyFile", e)
        ""
    }
    val headers: Map<String, String> = try {
        InputStreamReader(context.assets.open(headersFile)).use { it.readText() }
            .split("\n")
            .filter { it.isNotBlank() }
            .associate {
                it.substringBefore(delimiter = '=').trim() to
                    it.substringAfter(delimiter = '=', missingDelimiterValue = "").trim()
            }
    } catch (e: IOException) {
        Log.i("TapeAssetDispatcher", "Could not open headers file: $bodyFile", e)
        emptyMap()
    }
    return Tape(headers, body)
}

data class Tape(val headers: Map<String, String>, val body: String)

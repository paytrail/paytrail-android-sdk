package fi.paytrail.paymentsdk.test.server

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

annotation class ApiResponse(
    val method: RequestMethod = RequestMethod.GET,
    val path: String,
    val responseCode: Int = 200,
    val responseFile: String = "",
    val responseBody: String = "{}",
)

annotation class ApiError(
    val method: RequestMethod = RequestMethod.GET,
    val path: String,
    val responseCode: Int = 500,
    val errorCode: String = "",
    val errorBody: String = "{}",
)

class MockApiServer(private val defaultsBuilder: TapeAssetDispatcher.() -> Unit = {}) : TestRule {
    var mockWebServer: MockWebServer = MockWebServer()

    val port get() = mockWebServer.port

    override fun apply(base: Statement, description: Description): Statement {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = TapeAssetDispatcher().apply {
            invoke(defaultsBuilder)
        }

        return object : Statement() {
            override fun evaluate() {
                description.annotations
                    .filterIsInstance<ApiResponse>()
                    .forEach { annotation ->
                        (mockWebServer.dispatcher as TapeAssetDispatcher).invoke {
                            with(annotation) {
                                if (responseFile.isNotBlank()) {
                                    method requestOf path serves (responseCode withBodyFrom responseFile)
                                } else {
                                    method requestOf path serves (responseCode withBody responseBody)
                                }
                            }
                        }
                    }

                description.annotations
                    .filterIsInstance<ApiError>()
                    .forEach { annotation ->
                        (mockWebServer.dispatcher as TapeAssetDispatcher).invoke {
                            with(annotation) {
                                if (errorCode.isNotBlank()) {
                                    method requestOf path serves (responseCode withBody "")
                                } else {
                                    method requestOf path serves (responseCode withBody errorBody)
                                }
                            }
                        }
                    }

                mockWebServer.start(0)
                try {
                    base.evaluate()
                } finally {
                    mockWebServer.shutdown()
                }
            }
        }
    }
}

package ai.sahaj.gurukul.apidesign

import ai.sahaj.gurukul.apidesign.model.TextAnalysisOutput
import ai.sahaj.gurukul.apidesign.model.TextObjectInput
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals

@MicronautTest
class DemoTest {

    @Inject
    lateinit var application: EmbeddedServer

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }

    @Test
    fun testAnalyze() {
        val client: HttpClient = application.applicationContext.createBean(HttpClient::class.java, application.url)
        val sampleInput = TextObjectInput("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")
        val sampleOutput = TextAnalysisOutput(91,574,484,4,66)
        assertEquals(ObjectMapper().writeValueAsString(sampleOutput), client.toBlocking().retrieve(HttpRequest.POST("/text/analyze/", sampleInput)))
        client.close()
    }
}

package ai.sahaj.gurukul.apidesign.controller

import ai.sahaj.gurukul.apidesign.model.*
import ai.sahaj.gurukul.apidesign.service.TextService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/text")
class TextController {

    @Inject
    lateinit var textService: TextService

    @Post(value = "/analyze", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun analyze(@Body body: TextObjectInput): HttpResponse<TextAnalysisOutput> {
        return HttpResponse.ok(textService.analyze(body));
    }

}

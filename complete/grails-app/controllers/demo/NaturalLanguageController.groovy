package demo

import com.google.cloud.language.v1.Sentiment
import groovy.transform.CompileStatic
import org.grails.plugins.googlecloud.naturallanguage.GoogleCloudNaturalLanguageService

@CompileStatic
class NaturalLanguageController {

    GoogleCloudNaturalLanguageService googleCloudNaturalLanguageService

    def index(String text) {
        Sentiment sentiment = googleCloudNaturalLanguageService.sentiment(text)
        render "Text: ${text} Sentiment - score: ${sentiment.score} - magnitude: ${sentiment.magnitude}"
    }
}
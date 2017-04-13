package org.grails.plugins.googlecloud.naturallanguage

import com.google.cloud.language.spi.v1.LanguageServiceClient
import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.Sentiment
import groovy.transform.CompileStatic

@CompileStatic
class GoogleCloudNaturalLanguageService {
    LanguageServiceClient language = LanguageServiceClient.create()


    Sentiment sentiment(String text) {
        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        language.analyzeSentiment(doc).documentSentiment
    }
}
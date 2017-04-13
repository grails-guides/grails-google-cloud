package demo

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.plugins.googlecloud.translate.GoogleCloudTranslateService

@CompileStatic
class TranslateController {

    GoogleCloudTranslateService googleCloudTranslateService

    @CompileDynamic
    def index() {
        def translation = googleCloudTranslateService.translate(params.text, params.source, params.target)
        render "${params.text} => ${translation}"
    }
}

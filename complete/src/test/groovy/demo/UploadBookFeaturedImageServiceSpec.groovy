package demo

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class UploadBookFeaturedImageServiceSpec extends Specification implements ServiceUnitTest<UploadBookFeaturedImageService> {

    def "fileSuffix returns valid formatted string"() {
        expect:
        service.fileSuffix()
    }
}


package demo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

@SuppressWarnings('MethodName')
class BookSpec extends Specification implements DomainUnitTest<Book> {

    void 'book name is not nullable'() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
    }
}

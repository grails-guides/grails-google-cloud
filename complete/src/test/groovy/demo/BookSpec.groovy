package demo

import spock.lang.Specification
import grails.testing.gorm.DomainUnitTest

@SuppressWarnings('MethodName')
class BookSpec extends Specification implements DomainUnitTest<Book> {

    void 'book name is not nullable'() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
    }
}

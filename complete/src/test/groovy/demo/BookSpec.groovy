package demo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@SuppressWarnings('MethodName')
class BookSpec extends Specification implements DomainUnitTest<Book> {

    void 'book name is not nullable'() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
    }
}

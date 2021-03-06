package demo

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@SuppressWarnings('MethodName')
@TestFor(Book)
class BookSpec extends Specification {

    void 'book name is not nullable'() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
    }
}
